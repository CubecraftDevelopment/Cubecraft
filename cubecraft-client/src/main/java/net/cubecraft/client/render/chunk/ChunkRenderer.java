package net.cubecraft.client.render.chunk;

import ink.flybird.fcommon.event.EventHandler;
import ink.flybird.fcommon.registry.TypeItem;
import ink.flybird.quantum3d_legacy.Camera;
import ink.flybird.quantum3d_legacy.GLUtil;
import ink.flybird.quantum3d_legacy.culling.FrustumCuller;
import net.cubecraft.client.ClientSettingRegistry;
import net.cubecraft.client.ClientSharedContext;
import net.cubecraft.client.context.ClientRenderContext;
import net.cubecraft.client.render.RenderType;
import net.cubecraft.client.render.chunk.compile.ChunkCompileRequest;
import net.cubecraft.client.render.chunk.compile.ChunkCompileResult;
import net.cubecraft.client.render.chunk.compile.ChunkCompilerTask;
import net.cubecraft.client.render.chunk.layer.ChunkLayer;
import net.cubecraft.client.render.chunk.sort.ChunkCompileRequestSorter;
import net.cubecraft.client.render.chunk.sort.ChunkCompileResultSorter;
import net.cubecraft.client.render.chunk.sort.ChunkSorter;
import net.cubecraft.client.render.world.IWorldRenderer;
import net.cubecraft.event.BlockIDChangedEvent;
import net.cubecraft.event.SettingReloadEvent;
import org.joml.Vector3d;

import java.util.*;

@TypeItem("cubecraft:chunk_renderer")
public final class ChunkRenderer extends IWorldRenderer {
    public static final String SETTING_NAMESPACE = "chunk_renderer";
    public static final Map<String, ChunkLayer> DUMMY = net.cubecraft.client.context.ClientRenderContext.CHUNK_LAYER_RENDERER.createAll(false, RenderChunkPos.create(0, 0, 0));
    private final FrustumCuller frustum = new FrustumCuller();
    private final RenderList renderListAlpha = new RenderList(RenderType.ALPHA);
    private final RenderList renderListTransparent = new RenderList(RenderType.TRANSPARENT);

    private final Queue<ChunkCompileResult> resultQueue;
    private final Queue<ChunkCompileRequest> requestQueue;
    private final ChunkSorter chunkSorter;
    private final ChunkCompileRequestSorter chunkCompileRequestSorter;
    private final ChunkCompileResultSorter chunkCompileResultSorter;
    private Daemon daemon;
    private ChunkCompilerTask[] compilers;

    private ChunkStatusCache chunkStatusCache;


    public ChunkRenderer() {
        this.chunkSorter = new ChunkSorter();
        this.chunkCompileRequestSorter = new ChunkCompileRequestSorter(this.frustum);
        this.chunkCompileResultSorter = new ChunkCompileResultSorter(this.frustum);

        this.requestQueue = new PriorityQueue<>(this.chunkCompileRequestSorter);
        this.resultQueue = new PriorityQueue<>(this.chunkCompileResultSorter);
    }

    @Override
    public void init() {
        this.world.getEventBus().registerEventListener(this);
        int count = ClientSettingRegistry.CHUNK_UPDATE_THREAD.getValue();
        this.compilers = new ChunkCompilerTask[count];
        for (int i = 0; i < count; i++) {
            ChunkCompilerTask task = ChunkCompilerTask.daemon(this, this.requestQueue, this.resultQueue);
            this.compilers[i] = task;
            Thread t = new Thread(task);
            t.setDaemon(true);
            t.setPriority(1);
            t.start();
        }
        ClientSharedContext.QUERY_HANDLER.registerCallback(this.getID(), (arg -> switch (arg) {
            case "draw_size_alpha" -> this.renderListAlpha.size();
            case "draw_size_transparent" -> this.renderListTransparent.size();
            case "draw_success_size_alpha" -> this.renderListAlpha.getSuccessDrawCount();
            case "draw_success_size_transparent" -> this.renderListTransparent.getSuccessDrawCount();
            case "compile_result_size" -> this.resultQueue.size();
            case "compile_request_size" -> this.requestQueue.size();
            case "status_cache" -> this.chunkStatusCache.toString();
            default -> 0;
        }));
        this.chunkStatusCache = new ChunkStatusCache(new ChunkUpdateHandler(this));
        this.daemon = Daemon.create(this);
    }

    @Override
    public void tick() {
        Vector3d camPos = this.camera.getPosition();
        this.chunkSorter.setPos(camPos);
        this.chunkCompileRequestSorter.setPos(camPos);
        this.chunkCompileResultSorter.setPos(camPos);

        this.renderListTransparent.sort(this.chunkSorter);
        this.renderListAlpha.sort(this.chunkSorter);
        this.renderListAlpha.remove(this::isChunkOutOfRange);
        this.renderListTransparent.remove(this::isChunkOutOfRange);
    }

    @Override
    public void preRender() {
        int successCount = 0;
        for (int i = 0; i < ClientSettingRegistry.MAX_RECEIVE_COUNT.getValue(); i++) {
            if (successCount >= ClientSettingRegistry.MAX_UPLOAD_COUNT.getValue()) {
                return;
            }
            if (this.resultQueue.isEmpty()) {
                return;
            }
            ChunkCompileResult result = this.resultQueue.poll();
            if (result == null) {
                continue;
            }
            ChunkLayer layer = result.getLayer();
            RenderList renderList;
            if (DUMMY.get(result.getLayerId()).getRenderType() == RenderType.ALPHA) {
                renderList = this.renderListAlpha;
            } else {
                renderList = this.renderListTransparent;
            }
            if (result.isSuccess()) {
                result.upload();
                successCount++;
                renderList.putLayer(layer);
                this.chunkStatusCache.set(result.getPos(), ChunkUpdateStatus.UPDATE_SUCCESS);
            } else {
                renderList.removeLayer(result.getLayerId(), result.getPos());
                this.chunkStatusCache.set(result.getPos(), ChunkUpdateStatus.UPDATE_FAILED);
            }
        }
    }

    @Override
    public void preRender(RenderType type, float delta) {
        ClientRenderContext.TEXTURE.getTexture2DTileMapContainer().bind("cubecraft:terrain");
        this.camera.setUpGlobalCamera();
        this.frustum.calculateFrustum();
        if (type == RenderType.ALPHA) {
            this.renderListAlpha.updateVisibility(this::isChunkVisible);
        } else {
            this.renderListTransparent.updateVisibility(this::isChunkVisible);
        }
        this.parent.setFog(ClientSettingRegistry.getFixedViewDistance() * 16);
    }

    @Override
    public void render(RenderType type, float delta) {
        if (type == RenderType.ALPHA) {
            GLUtil.enableClientState();
            this.renderListAlpha.draw(this.camera.getPosition());
            GLUtil.disableClientState();
        } else {
            GLUtil.enableClientState();
            this.renderListTransparent.draw(this.camera.getPosition());
            GLUtil.disableClientState();
        }
    }

    @Override
    public void stop() {
        if (this.daemon != null) {
            this.daemon.setRunning(false);
        }
        if (this.compilers != null) {
            for (ChunkCompilerTask daemon : this.compilers) {
                daemon.setRunning(false);
            }
        }
        this.renderListAlpha.clear();
        this.renderListTransparent.clear();

        for (ChunkCompileResult result : this.resultQueue.toArray(new ChunkCompileResult[0])) {
            if (result == null) {
                continue;
            }
            if (result.isSuccess()) {
                result.upload();
            }
        }

        this.requestQueue.clear();
        this.resultQueue.clear();
        ClientSharedContext.QUERY_HANDLER.unregisterCallback(this.getID());
        this.world.getEventBus().registerEventListener(this);
    }

    public void gc() {
        try {
            if (!requestQueue.isEmpty()) {
                this.requestQueue.removeIf((o) -> Objects.isNull(o) || isChunkOutOfRange(o.getPos()));
            }
            if (!resultQueue.isEmpty()) {
                this.resultQueue.removeIf((o) -> Objects.isNull(o) || isChunkOutOfRange(o.getPos()));
            }
        } catch (ConcurrentModificationException ignored) {
        }
    }

    public void setUpdate(long x, long y, long z, boolean immediate) {
        setUpdate("cubecraft:alpha_block", x, y, z, immediate);
        setUpdate("cubecraft:transparent_block", x, y, z, immediate);
    }

    public void setUpdate(String layer, long x, long y, long z, boolean immediate) {
        RenderChunkPos pos = RenderChunkPos.create(x, y, z);
        ChunkCompileRequest request;
        request = ChunkCompileRequest.buildAt(this.world, pos, layer);
        if (immediate) {
            ChunkCompilerTask.task(this, request, this.resultQueue).run();
            return;
        }
        this.requestQueue.add(request);
    }

    public boolean isChunkOutOfRange(RenderChunkPos pos) {
        int dist = ClientSettingRegistry.getFixedViewDistance() * 16;
        return pos.chunkDistanceTo(this.player.getPosition()) > dist;
    }

    public boolean isChunkVisible(RenderChunkPos pos) {
        return this.frustum.aabbVisible(pos.getBounding(this.camera.getPosition()));
    }

    @EventHandler
    public void blockChanged(BlockIDChangedEvent e) {
        long x = e.x(), y = e.y(), z = e.z();
        long cx = x >> 4;
        long cy = y >> 4;
        long cz = z >> 4;

        if ((x & 15) == 0) {
            setUpdate(cx - 1, cy, cz, true);
        }
        if ((x & 15) == 15) {
            setUpdate(cx + 1, cy, cz, true);
        }
        if ((y & 15) == 0) {
            setUpdate(cx, cy - 1, cz, true);
        }
        if ((y & 15) == 15) {
            setUpdate(cx, cy + 1, cz, true);
        }
        if ((z & 15) == 0) {
            setUpdate(cx, cy, cz - 1, true);
        }
        if ((z & 15) == 15) {
            setUpdate(cx, cy, cz + 1, true);
        }
        setUpdate(cx, cy, cz, true);
    }

    @EventHandler
    public void onSettingReload(SettingReloadEvent e) {
        if (!e.isNodeChanged(SETTING_NAMESPACE)) {
            return;
        }
        this.refresh();
    }


    @SuppressWarnings("BusyWait")
    private static class Daemon extends Thread {
        public static final int ROTATION_SENSITIVE_VALUE = 3;
        public static final int DELAY_INTERVAL = 100;

        private final ChunkRenderer parent;
        private final Camera camera;
        boolean running = true;

        private long lastX = 0;
        private long lastY = 0;
        private long lastZ = 0;

        private double lastRotX = Double.MAX_VALUE;
        private double lastRotY = Double.MAX_VALUE;
        private double lastRotZ = Double.MAX_VALUE;
        private long last = 0L;

        private Daemon(ChunkRenderer parent) {
            this.parent = parent;
            this.camera = parent.getCamera();
        }

        public static Daemon create(ChunkRenderer parent) {
            Daemon daemon = new Daemon(parent);
            daemon.setDaemon(true);
            daemon.setName("chunk_check_daemon");
            daemon.setPriority(1);
            daemon.start();
            return daemon;
        }

        @Override
        public void run() {
            this.checkPosition();
            this.parent.chunkStatusCache.processUpdate();
            while (this.running) {
                if (System.currentTimeMillis() - last < DELAY_INTERVAL) {
                    try {
                        Thread.sleep(20);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    Thread.yield();
                }
                this.process();
                Thread.yield();
            }
        }

        public void process() {
            this.last = System.currentTimeMillis();
            this.parent.gc();
            if (this.checkPosition() || this.needRotationCheck()) {
                this.parent.chunkStatusCache.processUpdate();
            }
        }

        public boolean checkPosition() {
            long x = (long) (this.camera.getPosition().x) >> 4;
            long y = (long) (this.camera.getPosition().y) >> 4;
            long z = (long) (this.camera.getPosition().z) >> 4;

            if (ClientSettingRegistry.FORCE_REBUILD_NEAREST_CHUNK.getValue()) {
                this.parent.setUpdate(x, y, z, true);
            }

            boolean check = false;

            if (x != this.lastX) {
                this.lastX = x;
                check = true;
            }
            if (y != this.lastY) {
                this.lastY = y;
                check = true;
            }
            if (z != this.lastZ) {
                this.lastZ = z;
                check = true;
            }
            if (check) {
                this.parent.chunkStatusCache.moveTo(x, y, z);
            }

            return check;
        }

        public boolean needRotationCheck() {
            double xr = this.camera.getRotation().x;
            double yr = this.camera.getRotation().y;
            double zr = this.camera.getRotation().z;

            if (Math.abs(xr - this.lastRotX) > ROTATION_SENSITIVE_VALUE) {
                this.lastRotX = xr;
                return true;
            }
            if (Math.abs(yr - this.lastRotY) > ROTATION_SENSITIVE_VALUE) {
                this.lastRotY = yr;
                return true;
            }
            if (Math.abs(zr - this.lastRotZ) > ROTATION_SENSITIVE_VALUE) {
                this.lastRotZ = zr;
                return true;
            }

            return false;
        }

        public void setRunning(boolean running) {
            this.running = running;
        }
    }

    @SuppressWarnings("ClassCanBeRecord")
    private static class ChunkUpdateHandler implements ChunkStatusCache.UpdateHandler {
        private final ChunkRenderer renderer;

        private ChunkUpdateHandler(ChunkRenderer renderer) {
            this.renderer = renderer;
        }

        @Override
        public boolean apply(RenderChunkPos pos) {
            if (!this.renderer.isChunkVisible(pos)) {
                return false;
            }
            this.renderer.setUpdate(pos.getX(), pos.getY(), pos.getZ(), false);
            return true;
        }
    }
}