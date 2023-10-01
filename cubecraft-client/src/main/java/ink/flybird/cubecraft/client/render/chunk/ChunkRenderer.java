package ink.flybird.cubecraft.client.render.chunk;

import ink.flybird.cubecraft.client.ClientRenderContext;
import ink.flybird.cubecraft.client.ClientSharedContext;
import ink.flybird.cubecraft.client.internal.renderer.world.chunk.RenderChunk;
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
import ink.flybird.fcommon.container.ArrayQueue;
import ink.flybird.fcommon.container.KeyMap;
import ink.flybird.fcommon.event.EventHandler;
import ink.flybird.fcommon.math.MathHelper;
import ink.flybird.fcommon.registry.TypeItem;
import ink.flybird.quantum3d.device.Window;
import ink.flybird.quantum3d_legacy.Camera;
import ink.flybird.quantum3d_legacy.GLUtil;
import ink.flybird.quantum3d_legacy.culling.FrustumCuller;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

//todo:针对CPU优化（目标占用5%以内）
//todo: 共享的任务池（区块增删）（拉取队列后allocate）
//fixme: mipmap带来的一些方块变色

@TypeItem("cubecraft:terrain_renderer")
public final class ChunkRenderer extends IWorldRenderer {
    public static final String SETTING_NAMESPACE = "terrain_renderer";
    public static final Map<String, ChunkLayer> DUMMY = ClientRenderContext.CHUNK_LAYER_RENDERER.createAll(false, RenderChunkPos.create(0, 0, 0));

    public final KeyMap<RenderChunkPos, RenderChunk> chunks = new KeyMap<>();
    public final HashSet<String> posCache = new HashSet<>(16384);
    private final ChunkSorter chunkSorter;
    private final ChunkCompileRequestSorter chunkCompileRequestSorter;
    private final ChunkCompileResultSorter chunkCompileResultSorter;
    private final RenderList renderListAlpha = new RenderList(RenderType.ALPHA);
    private final RenderList renderListTransparent = new RenderList(RenderType.TRANSPARENT);
    private final ArrayQueue<ChunkCompileResult> resultQueue = new ArrayQueue<>();
    private final ArrayQueue<ChunkCompileRequest> requestQueue = new ArrayQueue<>();
    private final HashMap<String, ChunkLayer> alphaCallList = new HashMap<>();
    private final HashMap<String, ChunkLayer> transparentCallList = new HashMap<>();
    private final FrustumCuller frustum = new FrustumCuller(this.camera);
    private final AtomicInteger counter = new AtomicInteger();
    private final List<String> renderOrderList = new ArrayList<>();
    private ChunkCompilerTask[] daemons;

    public ChunkRenderer(Window window, IWorld world, EntityPlayer player, Camera cam, GameSetting setting) {
        super(window, world, player, cam, setting);
        this.chunkSorter = new ChunkSorter(this.frustum, this.camera, this.player);
        this.chunkCompileRequestSorter = new ChunkCompileRequestSorter(this.frustum, this.camera, this.player);
        this.chunkCompileResultSorter = new ChunkCompileResultSorter(this.frustum, this.camera, this.player);
    }


    public void init() {
        this.world.getEventBus().registerEventListener(this);
        ClientSharedContext.QUERY_HANDLER.registerCallback(this.getID(), (arg -> switch (arg) {
            case "pos_cache_size" -> this.posCache.size();
            case "draw_size_alpha" -> this.renderListAlpha.size();
            case "draw_size_transparent" -> this.renderListTransparent.size();
            case "draw_success_size_alpha" -> this.renderListAlpha.getSuccessDrawCount();
            case "draw_success_size_transparent" -> this.renderListTransparent.getSuccessDrawCount();
            case "compile_result_size" -> this.resultQueue.size();
            case "compile_request_size"->this.requestQueue.size();
            default -> 0;
        }));
    }

    @Override
    public void tick() {
        this.checkForAdd();
        try {
            this.renderListTransparent.sort(this.chunkSorter);
            this.renderListAlpha.sort(this.chunkSorter);
            this.requestQueue.sort(this.chunkCompileRequestSorter);
            this.resultQueue.sort(this.chunkCompileResultSorter);
        } catch (Exception ignored) {
        }

        this.renderListAlpha.remove(this::chunkRemove);
        this.renderListTransparent.remove(this::chunkRemove);
    }

    @Override
    public void preRender() {
        this.receiveUpdate();
        this.frustum.calculateFrustum();
        this.renderListAlpha.updateVisibility(this::chunkVisible);
        this.renderListTransparent.updateVisibility(this::chunkVisible);
    }

    @Override
    public void preRender(RenderType type, float delta) {
        this.counter.set(0);
        this.parent.setRenderState(this.setting);
        this.camera.setUpGlobalCamera(this.window);
        if (!(boolean) LevelRenderer.SettingHolder.CHUNK_FIX_DISTANCE.getValue()) {
            this.camera.setupGlobalTranslate();
        }
        ClientRenderContext.TEXTURE.getTexture2DTileMapContainer().bind("cubecraft:terrain");
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
    public void postRender(RenderType type, float delta) {
        this.parent.closeState(this.setting);
    }

    @Override
    public void refresh() {
        this.stop();
        int count = LevelRenderer.SettingHolder.CHUNK_UPDATE_THREAD.getValue();
        this.daemons = new ChunkCompilerTask[count];
        for (int i = 0; i < count; i++) {
            ChunkCompilerTask task = ChunkCompilerTask.daemon(this.requestQueue, this.resultQueue);
            this.daemons[i] = task;
            Thread t = new Thread(task);
            t.setDaemon(true);
            t.start();
        }
        this.checkForAdd();
    }

    @Override
    public void stop() {
        if (this.daemons != null) {
            for (ChunkCompilerTask daemon : this.daemons) {
                daemon.setRunning(false);
            }
        }
        this.alphaCallList.clear();
        this.transparentCallList.clear();
        this.requestQueue.clear();
        this.resultQueue.clear();
        this.posCache.clear();
    }


    public void receiveUpdate() {
        int successCount = 0;
        while (successCount < LevelRenderer.SettingHolder.MAX_UPLOAD_COUNT.getValue()) {
            if (this.resultQueue.size() <= 0) {
                break;
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
                renderList.removeLayer(result.getLayerId(),result.getPos());
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
                    if(this.chunkRemove(pos)){
                        continue;
                    }
                    if(!this.chunkVisible(pos)){
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
    }

    public void setUpdate(long x, long y, long z) {
        setUpdate("cubecraft:alpha_block", x, y, z);
        setUpdate("cubecraft:transparent_block", x, y, z);
    }

    public void setUpdate(String layer, long x, long y, long z) {
        String k = ChunkLayer.encode(layer, x, y, z);
        RenderChunkPos pos = RenderChunkPos.create(x, y, z);
        if (!DUMMY.containsKey(layer)) {
            return;
        }

        HashMap<String, ChunkLayer> callList;
        if (DUMMY.get(layer).getRenderType() == RenderType.ALPHA) {
            callList = this.alphaCallList;
        } else {
            callList = this.transparentCallList;
        }

        ChunkCompileRequest request;
        if (callList.containsKey(k)) {
            request = ChunkCompileRequest.rebuildAt(this.world, pos, callList.get(k));
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

    /*
        public void drawChunk(RenderType type, KeyMap<RenderChunkPos, RenderChunk> callList) {
            ArrayList<RenderChunk> list = new ArrayList<>(callList.map.values());
            list.sort(this.chunkSorterAndRemover);
            if (LevelRenderer.SettingHolder.CHUNK_FIX_DISTANCE.getValue()) {
                for (RenderChunk chunk : list) {
                    if (!this.camera.objectDistanceSmallerThan(chunk.getKey().getWorldPosition(), LevelRenderer.SettingHolder.CHUNK_RENDER_DISTANCE.getValue() * 16)) {
                        continue;
                    }

                    GL11.glPushMatrix();
                    this.camera.setupObjectCamera(chunk.getKey().getWorldPosition());
                    if (!this.frustum.aabbVisible(chunk.getVisibleArea(this.camera))) {
                        GL11.glPopMatrix();
                        continue;
                    }
                    if (!chunk.anyLayerFilled(type)) {
                        GL11.glPopMatrix();
                        continue;
                    }
                    chunk.render(type);
                    counter.addAndGet(1);
                    GL11.glPopMatrix();
                }
                GLUtil.checkError("draw chunks:" + type);

                if (type == RenderType.ALPHA) {
                    if (this.setting.getValueAsBoolean("client.render.terrain.use_occlusion", false)) {
                        GLUtil.setDrawOutput(false);
                        GLUtil.enableBlend();


                        GL11.glDepthMask(false);
                        if (!this.occlusionCuller._listVisible(chunk.getVisibleAreaCall())) {
                            GLUtil.setDrawOutput(true);
                            GL11.glPopMatrix();
                            continue;
                        }
                        GLUtil.setDrawOutput(true);
                    }
                }

            } else {
                IntBuffer buffer = BufferAllocation.allocIntBuffer(list.size());

                for (RenderChunk chunk : list) {
                    if (!this.camera.objectDistanceSmallerThan(chunk.getKey().getWorldPosition(), LevelRenderer.SettingHolder.CHUNK_RENDER_DISTANCE.getValue() * 16)) {
                        continue;
                    }
                    if (!this.frustum.aabbVisible(chunk.getVisibleArea(this.camera))) {
                        continue;
                    }
                    buffer.put(chunk.getRenderLists(type));
                }

                buffer.flip().slice();
                GL11.glCallLists(buffer);
                BufferAllocation.free(buffer);
            }
        }

    private void updateChunks() {
        for (int i = 0; i < LevelRenderer.SettingHolder.MAX_UPLOAD_COUNT.getValue(); i++) {
            DrawCompile<RenderChunk> compile = this.updateService.getAvailableCompile();
            if (this.updateService.getResultSize() <= 0) {
                break;
            }

            if (compile == null) {
                continue;
            }
            RenderChunk chunk = compile.getObject();
            if (!chunk.getLifetimeCounter().isAllocated()) {
                chunk.allocate();
            }
            compile.draw();
        }

        while (this.updateService.getAllResultSize() > 0) {
            IDrawCompile<RenderChunk> compile = this.updateService.getAllCompile();

            if (compile != null) {
                RenderChunk chunk = (compile.getObject());
                this.checkCallList(chunk, RenderType.ALPHA, this.callListAlpha);
                this.checkCallList(chunk, RenderType.TRANSPARENT, this.callListTransParent);
                boolean a = chunk.anyLayerFilled(RenderType.ALPHA);
                boolean t = chunk.anyLayerFilled(RenderType.TRANSPARENT);
                if ((a || t) && !this.chunks.contains(chunk.pos)) {
                    this.chunks.add(chunk);
                } else {
                    //chunk.destroy();
                }
            } else {

            }
        }
    }

    public void checkChunkCache() {
        int dist = LevelRenderer.SettingHolder.CHUNK_RENDER_DISTANCE.getValue();

        if (this.updateService == null) {
            return;
        }

        /*
        long CX = (long) (this.camera.getPosition().x() / 16);
        long CY = (long) (this.camera.getPosition().y() / 16);
        long CZ = (long) (this.camera.getPosition().z() / 16);
        long lastCX = (long) (this.camera.getLastPosition().x() / 16);
        long lastCY = (long) (this.camera.getLastPosition().y() / 16);
        long lastCZ = (long) (this.camera.getLastPosition().z() / 16);

        if (CX == lastCX && CY == lastCY && CZ == lastCZ && !camera.isRotationChanged()) {
            //return;
        }


        long playerCX = (long) (this.camera.getPosition().x / 16);
        long playerCZ = (long) (this.camera.getPosition().z / 16);
        long playerCY = (long) (this.camera.getPosition().y / 16);
        for (long cx = playerCX - dist; cx <= playerCX + dist; cx++) {
            for (long cz = playerCZ - dist; cz <= playerCZ + dist; cz++) {
                long y1 = MathHelper.clamp(playerCY - dist, Chunk.HEIGHT / Chunk.WIDTH + 1, -1);
                long y2 = MathHelper.clamp(playerCY + dist, Chunk.HEIGHT / Chunk.WIDTH + 1, -1);
                for (long cy = y1; cy <= y2; cy++) {
                    RenderChunkPos pos = RenderChunkPos.create(cx, cy, cz);
                    if (!this.camera.objectDistanceSmallerThan(pos.getWorldPosition(), dist * 16)) {
                        continue;
                    }
                    if (this.posCache.containsKey(pos.toString())) {
                        continue;
                    }
                    if (!this.frustum.aabbVisible(RenderChunkPos.getAABBFromPos(pos, this.camera))) {
                        continue;
                    }
                    this.posCache.put(pos.toString(), null);
                    RenderChunk chunk = new RenderChunk(this, this.world, pos);
                    this.updateService.startDrawing(chunk);
                }
            }
        }

        Iterator<RenderChunk> iterator = this.chunks.map.values().iterator();
        while (iterator.hasNext()) {
            RenderChunk c = iterator.next();
            if (this.camera.objectDistanceSmallerThan(c.getKey().getWorldPosition(), dist * 16)) {
                continue;
            }
            this.callListTransParent.remove(c.getKey());
            this.callListAlpha.remove(c.getKey());
            this.updateService.getCache().remove(c);
            //c.destroy();
            this.posCache.remove(c.getKey().toString());
            iterator.remove();
        }

        Iterator<String> iterator2 = this.posCache.keySet().iterator();
        while (iterator2.hasNext()) {
            RenderChunkPos pos = new RenderChunkPos(iterator2.next());
            if (this.camera.objectDistanceSmallerThan(pos.getWorldPosition(), dist * 16)) {
                continue;
            }
            iterator2.remove();
        }

        if (this.updateService.getCache().size() < 16) {
            return;
        }
        try {
            this.updateService.getCache().sort(this.chunkSorterAndRemover);
        } catch (Exception ignored) {
        }
    }

        public void checkCallList(RenderChunk chunk, RenderType type, KeyMap<RenderChunkPos, RenderChunk> callList) {
        if (chunk.anyLayerFilled(type)) {
            if (!callList.contains(chunk.getKey())) {
                callList.add(chunk);
            }
        } else {
            //callList.remove(chunk.getKey());
        }
    }
    */

    public Boolean chunkRemove(RenderChunkPos pos) {
        int dist = LevelRenderer.SettingHolder.CHUNK_RENDER_DISTANCE.getValue() * 16;
        return pos.distanceTo(this.player)>dist;
    }

    public Boolean chunkVisible(RenderChunkPos pos){
        return this.frustum.aabbVisible(RenderChunkPos.getAABBFromPos(pos,this.camera));
    }
}