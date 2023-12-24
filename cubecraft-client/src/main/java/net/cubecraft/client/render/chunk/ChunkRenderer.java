package net.cubecraft.client.render.chunk;

import ink.flybird.fcommon.event.EventHandler;
import ink.flybird.fcommon.math.MathHelper;
import ink.flybird.fcommon.registry.TypeItem;
import ink.flybird.quantum3d_legacy.Camera;
import ink.flybird.quantum3d_legacy.GLUtil;
import ink.flybird.quantum3d_legacy.culling.FrustumCuller;

import me.gb2022.quantum3d.device.Window;

import net.cubecraft.client.ClientRenderContext;
import net.cubecraft.client.ClientSettingRegistry;
import net.cubecraft.client.ClientSharedContext;
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
import net.cubecraft.internal.entity.EntityPlayer;
import net.cubecraft.world.IWorld;
import net.cubecraft.world.chunk.Chunk;
import org.joml.Vector3d;

import java.util.*;

@TypeItem("cubecraft:chunk_renderer")
public final class ChunkRenderer extends IWorldRenderer {
    public static final String SETTING_NAMESPACE = "chunk_renderer";
    public static final Map<String, ChunkLayer> DUMMY = ClientRenderContext.CHUNK_LAYER_RENDERER.createAll(false, RenderChunkPos.create(0, 0, 0));
    public final HashMap<RenderChunkPos, ChunkUpdateStatus> posCache = new HashMap<>(16384);
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

    public ChunkRenderer(Window window, IWorld world, EntityPlayer player, Camera cam) {
        super(window, world, player, cam);
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
            ChunkCompilerTask task = ChunkCompilerTask.daemon(this.requestQueue, this.resultQueue);
            this.compilers[i] = task;
            Thread t = new Thread(task);
            t.setDaemon(true);
            t.start();
        }
        ClientSharedContext.QUERY_HANDLER.registerCallback(this.getID(), (arg -> switch (arg) {
            case "pos_cache_size" -> this.posCache.size();
            case "draw_size_alpha" -> this.renderListAlpha.size();
            case "draw_size_transparent" -> this.renderListTransparent.size();
            case "draw_success_size_alpha" -> this.renderListAlpha.getSuccessDrawCount();
            case "draw_success_size_transparent" -> this.renderListTransparent.getSuccessDrawCount();
            case "compile_result_size" -> this.resultQueue.size();
            case "compile_request_size" -> this.requestQueue.size();
            default -> 0;
        }));
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
        this.renderListAlpha.remove(this::chunkRemove);
        this.renderListTransparent.remove(this::chunkRemove);
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
                this.posCache.put(result.getPos(), ChunkUpdateStatus.CHECKED);
            } else {
                renderList.removeLayer(result.getLayerId(), result.getPos());
                this.posCache.put(result.getPos(), ChunkUpdateStatus.UPDATE_FAILED);
            }
        }
    }

    @Override
    public void preRender(RenderType type, float delta) {
        ClientRenderContext.TEXTURE.getTexture2DTileMapContainer().bind("cubecraft:terrain");
        this.camera.setUpGlobalCamera();
        this.frustum.calculateFrustum();
        if (type == RenderType.ALPHA) {
            this.renderListAlpha.updateVisibility(this::chunkVisible);
        } else {
            this.renderListTransparent.updateVisibility(this::chunkVisible);
        }
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
        this.posCache.clear();
        ClientSharedContext.QUERY_HANDLER.unregisterCallback(this.getID());
        this.world.getEventBus().registerEventListener(this);
    }

    private void updateRenderer() {
        List<RenderChunkPos> cache = new ArrayList<>();
        int dist = ClientSettingRegistry.getFixedViewDistance();
        long playerCX = (long) ((this.camera.getPosition().x + 8) / 16);
        long playerCZ = (long) ((this.camera.getPosition().z + 8) / 16);
        long playerCY = (long) ((this.camera.getPosition().y + 8) / 16);
        for (long cx = playerCX - dist; cx <= playerCX + dist; cx++) {
            for (long cz = playerCZ - dist; cz <= playerCZ + dist; cz++) {
                long y1 = MathHelper.clamp(playerCY - dist, Chunk.HEIGHT / Chunk.WIDTH + 1, -1);
                long y2 = MathHelper.clamp(playerCY + dist, Chunk.HEIGHT / Chunk.WIDTH + 1, -1);
                for (long cy = y1; cy <= y2; cy++) {
                    RenderChunkPos pos = RenderChunkPos.create(cx, cy, cz);
                    if (this.chunkRemove(pos)) {
                        continue;
                    }
                    if (!this.chunkVisible(pos)) {
                        continue;
                    }
                    if (this.posCache.getOrDefault(pos, ChunkUpdateStatus.UNCHECKED) != ChunkUpdateStatus.UNCHECKED) {
                        continue;
                    }
                    this.posCache.put(pos, ChunkUpdateStatus.CHECKED);
                    cache.add(new RenderChunkPos(cx, cy, cz));
                }
            }
        }

        if (ClientSettingRegistry.FORCE_REBUILD_NEAREST_CHUNK.getValue()) {
            setUpdate(playerCX, playerCY, playerCZ, false);
        }

        cache.sort(this.chunkSorter);

        for (int i = cache.size() - 1; i >= 0; i--) {
            RenderChunkPos pos = cache.get(i);
            setUpdate(pos.getX(), pos.getY(), pos.getZ(), false);
        }
    }

    public void gc(){
        try {
            Set<RenderChunkPos> removePositionList = new HashSet<>();
            if (!requestQueue.isEmpty()) {
                this.requestQueue.removeIf(req -> {
                    if (req == null) {
                        return true;
                    }
                    boolean b = chunkRemove(req.getPos());
                    if (b) {
                        removePositionList.add(req.getPos());
                    }
                    return b;
                });
            }
            if (!resultQueue.isEmpty()) {
                this.resultQueue.removeIf(res -> {
                    if (res == null) {
                        return true;
                    }
                    boolean b = chunkRemove(res.getPos());
                    if (b) {
                        removePositionList.add(res.getPos());
                    }
                    return b;
                });
            }
            this.posCache.forEach((k, v) -> {
                if (this.chunkRemove(k)) removePositionList.add(k);
            });
            for (RenderChunkPos pos : removePositionList) {
                this.posCache.remove(pos);
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
        this.posCache.put(pos, ChunkUpdateStatus.CHECKED);
        String k = ChunkLayer.encode(layer, x, y, z);

        if (!DUMMY.containsKey(layer)) {
            return;
        }

        RenderList callList;
        if (DUMMY.get(layer).getRenderType() == RenderType.ALPHA) {
            callList = this.renderListAlpha;
        } else {
            callList = this.renderListTransparent;
        }

        ChunkCompileRequest request;
        if (callList.containsLayer(k)) {
            request = ChunkCompileRequest.rebuildAt(this.world, pos, callList.getLayer(k));
        } else {
            request = ChunkCompileRequest.buildAt(this.world, pos, layer);
        }
        if (immediate) {
            ChunkCompilerTask.task(request, this.resultQueue).run();
            return;
        }
        if (!this.requestQueue.contains(request)) {
            this.requestQueue.add(request);
        }
    }

    public Boolean chunkRemove(RenderChunkPos pos) {
        int dist = ClientSettingRegistry.getFixedViewDistance() * 16;
        return pos.distanceTo(this.player) > dist;
    }

    public Boolean chunkVisible(RenderChunkPos pos) {
        return this.frustum.aabbVisible(pos.getAABB(this.camera.getPosition()));
    }

    @EventHandler
    public void blockChanged(BlockIDChangedEvent e) {
        long x = e.x(), y = e.y(), z = e.z();
        long cx = x >> 4;
        long cy = y >> 4;
        long cz = z >> 4;
        if (MathHelper.getRelativePosInChunk(x, 16) == 0) {
            setUpdate(cx - 1, cy, cz, true);
        }
        if (MathHelper.getRelativePosInChunk(x, 16) == 15) {
            setUpdate(cx + 1, cy, cz, true);
        }
        if (MathHelper.getRelativePosInChunk(y, 16) == 0) {
            setUpdate(cx, cy - 1, cz, true);
        }
        if (MathHelper.getRelativePosInChunk(y, 16) == 15) {
            setUpdate(cx, cy + 1, cz, true);
        }
        if (MathHelper.getRelativePosInChunk(z, 16) == 0) {
            setUpdate(cx, cy, cz - 1, true);
        }
        if (MathHelper.getRelativePosInChunk(z, 16) == 15) {
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

        private Daemon(ChunkRenderer parent) {
            this.parent = parent;
            this.camera = parent.getCamera();
        }

        public static Daemon create(ChunkRenderer parent) {
            Daemon daemon = new Daemon(parent);
            daemon.setDaemon(true);
            daemon.setName("chunk_renderer_daemon");
            daemon.start();

            return daemon;
        }

        @Override
        public void run() {
            long last = 0L;
            while (this.running) {
                while (System.currentTimeMillis() - last < DELAY_INTERVAL) {
                    try {
                        Thread.yield();
                        Thread.sleep(10);
                        Thread.yield();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                Thread.yield();
                last = System.currentTimeMillis();
                if (checkForUpdate()) {
                    this.parent.updateRenderer();
                }
                this.parent.gc();
                Thread.yield();
            }
        }

        public boolean checkForUpdate() {
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

            long x = (long) ((this.camera.getPosition().x + 8) / 16);
            long y = (long) ((this.camera.getPosition().y + 8) / 16);
            long z = (long) ((this.camera.getPosition().z + 8) / 16);

            if (x != this.lastX) {
                this.lastX = x;
                return true;
            }
            if (y != this.lastY) {
                this.lastY = y;
                return true;
            }
            if (z != this.lastZ) {
                this.lastZ = z;
                return true;
            }
            return false;
        }

        public void setRunning(boolean running) {
            this.running = running;
        }
    }
}