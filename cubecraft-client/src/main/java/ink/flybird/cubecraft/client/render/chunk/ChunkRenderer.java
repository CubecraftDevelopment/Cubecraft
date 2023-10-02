package ink.flybird.cubecraft.client.render.chunk;

import ink.flybird.cubecraft.client.ClientRenderContext;
import ink.flybird.cubecraft.client.ClientSharedContext;
import ink.flybird.cubecraft.client.render.LevelRenderer;
import ink.flybird.cubecraft.client.render.RenderType;
import ink.flybird.cubecraft.client.render.chunk.compile.ChunkCompileRequest;
import ink.flybird.cubecraft.client.render.chunk.compile.ChunkCompileResult;
import ink.flybird.cubecraft.client.render.chunk.compile.ChunkCompilerTask;
import ink.flybird.cubecraft.client.render.chunk.layer.ChunkLayer;
import ink.flybird.cubecraft.client.render.chunk.sort.ChunkCompileRequestSorter;
import ink.flybird.cubecraft.client.render.chunk.sort.ChunkCompileResultSorter;
import ink.flybird.cubecraft.client.render.chunk.sort.ChunkSorter;
import ink.flybird.cubecraft.client.render.world.IWorldRenderer;
import ink.flybird.cubecraft.event.SettingReloadEvent;
import ink.flybird.cubecraft.internal.entity.EntityPlayer;
import ink.flybird.cubecraft.world.IWorld;
import ink.flybird.cubecraft.world.chunk.Chunk;
import ink.flybird.cubecraft.world.event.BlockIDChangedEvent;
import ink.flybird.fcommon.GameSetting;
import ink.flybird.fcommon.event.EventHandler;
import ink.flybird.fcommon.math.MathHelper;
import ink.flybird.fcommon.registry.TypeItem;
import ink.flybird.quantum3d.device.Window;
import ink.flybird.quantum3d_legacy.Camera;
import ink.flybird.quantum3d_legacy.GLUtil;
import ink.flybird.quantum3d_legacy.culling.FrustumCuller;
import org.joml.Vector3d;

import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.Map;
import java.util.PriorityQueue;

//todo:针对CPU优化（目标占用5%以内）
//todo: 共享的任务池（区块增删）（拉取队列后allocate）
//fixme: mipmap带来的一些方块变色

@TypeItem("cubecraft:chunk_renderer")
public final class ChunkRenderer extends IWorldRenderer {
    public static final String SETTING_NAMESPACE = "chunk_renderer";
    public static final Map<String, ChunkLayer> DUMMY = ClientRenderContext.CHUNK_LAYER_RENDERER.createAll(false, RenderChunkPos.create(0, 0, 0));
    public final HashSet<String> posCache = new HashSet<>(16384);

    private final FrustumCuller frustum = new FrustumCuller();
    private final RenderList renderListAlpha = new RenderList(RenderType.ALPHA);
    private final RenderList renderListTransparent = new RenderList(RenderType.TRANSPARENT);

    private final PriorityQueue<ChunkCompileResult> resultQueue;
    private final PriorityQueue<ChunkCompileRequest> requestQueue;
    private final ChunkSorter chunkSorter;
    private final ChunkCompileRequestSorter chunkCompileRequestSorter;
    private final ChunkCompileResultSorter chunkCompileResultSorter;
    private Daemon daemon=new Daemon();
    private ChunkCompilerTask[] daemons;

    public ChunkRenderer(Window window, IWorld world, EntityPlayer player, Camera cam, GameSetting setting) {
        super(window, world, player, cam, setting);
        this.chunkSorter = new ChunkSorter();
        this.chunkCompileRequestSorter = new ChunkCompileRequestSorter(this.frustum);
        this.chunkCompileResultSorter = new ChunkCompileResultSorter(this.frustum);

        this.requestQueue = new PriorityQueue<>(this.chunkCompileRequestSorter);
        this.resultQueue = new PriorityQueue<>(this.chunkCompileResultSorter);
    }


    public void init() {
        this.world.getEventBus().registerEventListener(this);
        int count = LevelRenderer.SettingHolder.CHUNK_UPDATE_THREAD.getValue();
        this.daemons = new ChunkCompilerTask[count];
        for (int i = 0; i < count; i++) {
            ChunkCompilerTask task = ChunkCompilerTask.daemon(this.requestQueue, this.resultQueue);
            this.daemons[i] = task;
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
        this.daemon = new Daemon();
        this.daemon.setDaemon(true);
        this.daemon.setName("chunk_renderer_daemon");
        this.daemon.start();
    }

    @Override
    public void tick() {
        this.renderListTransparent.sort(this.chunkSorter);
        this.renderListAlpha.sort(this.chunkSorter);
        this.renderListAlpha.remove(this::chunkRemove);
        this.renderListTransparent.remove(this::chunkRemove);
    }

    @Override
    public void preRender() {
        this.receiveUpdate();
    }

    @Override
    public void preRender(RenderType type, float delta) {
        this.camera.setUpGlobalCamera(this.window);
        this.frustum.calculateFrustum();
        ClientRenderContext.TEXTURE.getTexture2DTileMapContainer().bind("cubecraft:terrain");
        if (type == RenderType.ALPHA) {
            this.renderListAlpha.updateVisibility(this::chunkVisible);
            return;
        }
        this.renderListTransparent.updateVisibility(this::chunkVisible);
    }

    @Override
    public void render(RenderType type, float delta) {
        if (type == RenderType.ALPHA) {
            GLUtil.enableAlphaTest();
            this.renderListAlpha.draw(this.camera.getPosition());
            GLUtil.disableAlphaTest();
            return;
        }
        if (type == RenderType.TRANSPARENT) {
            this.renderListTransparent.draw(this.camera.getPosition());
        }
    }

    @Override
    public void stop() {
        this.daemon.setRunning(false);
        if (this.daemons != null) {
            for (ChunkCompilerTask daemon : this.daemons) {
                daemon.setRunning(false);
            }
        }
        this.renderListAlpha.clear();
        this.renderListTransparent.clear();
        while (!this.resultQueue.isEmpty()) {
            ChunkCompileResult result = this.resultQueue.poll();
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


    public void receiveUpdate() {
        int successCount = 0;
        for (int i = 0; i < LevelRenderer.SettingHolder.MAX_RECEIVE_COUNT.getValue(); i++) {
            if (successCount >= LevelRenderer.SettingHolder.MAX_UPLOAD_COUNT.getValue()) {
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
            } else {
                renderList.removeLayer(result.getLayerId(), result.getPos());
            }
        }
    }

    public void checkForAdd() {
        int dist = LevelRenderer.SettingHolder.CHUNK_RENDER_DISTANCE.getValue();
        long playerCX = (long) (this.camera.getPosition().x / 16);
        long playerCZ = (long) (this.camera.getPosition().z / 16);
        long playerCY = (long) (this.camera.getPosition().y / 16);
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
                    if (this.posCache.contains(pos.toString())) {
                        continue;
                    }
                    this.posCache.add(pos.toString());
                    setUpdate(cx, cy, cz);
                }
            }
        }

        if (LevelRenderer.SettingHolder.FORCE_REBUILD_NEAREST_CHUNK.getValue()) {
            setUpdate(playerCX, playerCY, playerCZ);
        }
    }

    public void setUpdate(long x, long y, long z) {
        setUpdate("cubecraft:alpha_block", x, y, z);
        setUpdate("cubecraft:transparent_block", x, y, z);
    }

    public void setUpdate(String layer, long x, long y, long z) {
        RenderChunkPos pos = RenderChunkPos.create(x, y, z);
        if (!this.posCache.contains(pos.toString())) {
            this.posCache.add(pos.toString());
        }
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
        if (!this.requestQueue.contains(request)) {
            this.requestQueue.add(request);
        }
    }


    @EventHandler
    public void blockChanged(BlockIDChangedEvent e) {
        long x = e.x(), y = e.y(), z = e.z();
        long cx = MathHelper.getChunkPos(x, 16);
        long cy = MathHelper.getChunkPos(y, 16);
        long cz = MathHelper.getChunkPos(z, 16);
        if (MathHelper.getRelativePosInChunk(x, 16) == 0) {
            setUpdate(cx - 1, cy, cz);
        }
        if (MathHelper.getRelativePosInChunk(x, 16) == 15) {
            setUpdate(cx + 1, cy, cz);
        }
        if (MathHelper.getRelativePosInChunk(y, 16) == 0) {
            setUpdate(cx, cy - 1, cz);
        }
        if (MathHelper.getRelativePosInChunk(y, 16) == 15) {
            setUpdate(cx, cy + 1, cz);
        }
        if (MathHelper.getRelativePosInChunk(z, 16) == 0) {
            setUpdate(cx, cy, cz - 1);
        }
        if (MathHelper.getRelativePosInChunk(z, 16) == 15) {
            setUpdate(cx, cy, cz + 1);
        }
        setUpdate(cx, cy, cz);
    }

    @EventHandler
    public void onSettingReload(SettingReloadEvent e) {
        if (!e.isNodeChanged(SETTING_NAMESPACE)) {
            return;
        }
        this.refresh();
    }

    public Boolean chunkRemove(RenderChunkPos pos) {
        int dist = LevelRenderer.SettingHolder.CHUNK_RENDER_DISTANCE.getValue() * 16;
        return pos.distanceTo(this.player) > dist;
    }

    public Boolean chunkVisible(RenderChunkPos pos) {
        return this.frustum.aabbVisible(pos.getAABB(this.camera.getPosition()));
    }

    private void updateRenderer() {
        int dist = LevelRenderer.SettingHolder.CHUNK_RENDER_DISTANCE.getValue();
        this.checkForAdd();
        Vector3d camPos = this.camera.getPosition();
        this.chunkSorter.setPos(camPos);
        this.chunkCompileRequestSorter.setPos(camPos);
        this.chunkCompileResultSorter.setPos(camPos);
        try {
            if (!requestQueue.isEmpty()) {
                this.requestQueue.removeIf(req -> req == null || chunkRemove(req.getPos()));
            }
            if (!resultQueue.isEmpty()) {
                this.resultQueue.removeIf(res -> res == null || chunkRemove(res.getPos()));
            }
            this.posCache.removeIf(s -> new RenderChunkPos(s).distanceTo(this.camera.getPosition()) > dist * 16);
        } catch (ConcurrentModificationException ignored) {
        }
    }

    private class Daemon extends Thread {
        boolean running = true;

        @Override
        public void run() {
            while (this.running) {
                updateRenderer();
            }
        }

        public void setRunning(boolean running) {
            this.running = running;
        }
    }
}