package ink.flybird.cubecraft.client.internal.renderer.world;

import ink.flybird.cubecraft.client.ClientRenderContext;
import ink.flybird.cubecraft.client.internal.registry.ClientSettingRegistry;
import ink.flybird.cubecraft.client.internal.renderer.world.chunk.RenderChunk;
import ink.flybird.cubecraft.client.internal.renderer.world.chunk.RenderChunkPos;
import ink.flybird.quantum3d.Camera;
import ink.flybird.quantum3d.GLUtil;
import ink.flybird.quantum3d.compile.IDrawService;
import ink.flybird.quantum3d.compile.MultiRenderCompileService;
import ink.flybird.quantum3d.culling.FrustumCuller;
import ink.flybird.quantum3d.culling.OcclusionCuller;
import ink.flybird.quantum3d.draw.*;
import ink.flybird.quantum3d.device.Window;
import ink.flybird.fcommon.GameSetting;
import ink.flybird.fcommon.container.CollectionUtil;
import ink.flybird.fcommon.container.KeyMap;
import ink.flybird.fcommon.event.EventHandler;

import ink.flybird.fcommon.math.MathHelper;
import ink.flybird.fcommon.registry.TypeItem;
import ink.flybird.cubecraft.client.ClientSharedContext;
import ink.flybird.cubecraft.client.internal.renderer.world.chunk.ChunkSorter;
import ink.flybird.cubecraft.client.render.RenderType;
import ink.flybird.cubecraft.client.render.renderer.IWorldRenderer;
import io.flybird.cubecraft.event.SettingReloadEvent;
import io.flybird.cubecraft.internal.entity.EntityPlayer;
import io.flybird.cubecraft.world.IWorld;
import io.flybird.cubecraft.world.chunk.Chunk;
import io.flybird.cubecraft.world.event.BlockIDChangedEvent;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;


//todo:pos_cache_clearing
//todo: thread chunk check

//todo:region callList copy(4*4*4)
//todo:remake render system
//todo:修复世界刷新后区块不渲染的问题

// todo:mipmap带来的一些方块变色

@TypeItem("cubecraft:terrain_renderer")
public class TerrainRenderer extends IWorldRenderer {
    public static final boolean DISTANCE_FIX = true;
    private static final String SETTING_NAMESPACE = "client.render.terrain";
    public final KeyMap<RenderChunkPos, RenderChunk> chunks = new KeyMap<>();
    public final HashMap<String, Object> posCache = new HashMap<>(16384);

    public final KeyMap<RenderChunkPos, RenderChunk> callListAlpha = new KeyMap<>();
    public final KeyMap<RenderChunkPos, RenderChunk> callListTransParent = new KeyMap<>();

    private final FrustumCuller frustum = new FrustumCuller(this.camera);
    private final OcclusionCuller occlusionCuller = new OcclusionCuller(this.camera);
    private final ChunkSorter chunkSorterAndRemover;
    public IDrawService<RenderChunk> updateService;
    AtomicInteger counter = new AtomicInteger();
    private int renderDistance;

    public TerrainRenderer(Window window, IWorld world, EntityPlayer player, Camera cam, GameSetting setting) {
        super(window, world, player, cam, setting);
        world.getEventBus().registerEventListener(this);
        this.updateService = new MultiRenderCompileService<>(setting.getValueAsInt("client.render.terrain.draw_thread", 1));
        this.chunkSorterAndRemover = new ChunkSorter(this.frustum, this.camera, this.player);
    }

    public void init() {
        ClientSharedContext.QUERY_HANDLER.registerCallback(this.getID(), (arg -> switch (arg) {
            case "chunk_cache_size" -> this.chunks.size();
            case "visible_chunk_size_alpha" -> this.callListAlpha.size();
            default -> 6;
        }));
    }


    //world renderer
    @Override
    public void preRender() {
        this.updateChunks();
        //if (System.currentTimeMillis() % 50 == 0) {
            this.checkChunkCache();
        //}
        this.frustum.calculateFrustum();
    }

    @Override
    public void preRender(RenderType type, float delta) {
        this.counter.set(0);
        this.parent.setRenderState(this.setting);
        this.camera.setUpGlobalCamera(this.window);
        if (!DISTANCE_FIX) {
            this.camera.setupGlobalTranslate();
        }
        ClientRenderContext.TEXTURE.getTexture2DTileMapContainer().bind("cubecraft:terrain");
    }

    @Override
    public void render(RenderType type, float delta) {
        if (type == RenderType.ALPHA) {
            GLUtil.enableAlphaTest();
            this.drawChunk(type, this.callListAlpha);
            GLUtil.disableAlphaTest();
            return;
        }
        if (type == RenderType.TRANSPARENT) {
            this.drawChunk(type, this.callListTransParent);
        }
    }

    @Override
    public void postRender(RenderType type, float delta) {
        this.parent.closeState(this.setting);
    }

    @Override
    public void refresh() {
        this.stop();
        this.updateService = new MultiRenderCompileService<>(4);
        this.checkChunkCache();
    }

    @Override
    public void stop() {
        CollectionUtil.iterateMap(this.chunks.map, (key, item) -> item.destroy());
        this.callListAlpha.clear();
        this.callListTransParent.clear();
        this.chunks.clear();
        this.posCache.clear();

        if (this.updateService == null) {
            return;
        }
        this.updateService.stop();
        this.updateService = null;
    }


    public void drawChunk(RenderType type, KeyMap<RenderChunkPos, RenderChunk> callList) {
        ArrayList<RenderChunk> list = new ArrayList<>(callList.map.values());
        list.sort(this.chunkSorterAndRemover);
        for (RenderChunk chunk : list) {
            if (!this.camera.objectDistanceSmallerThan(chunk.getKey().clipToWorldPosition(), ClientSettingRegistry.CHUNK_RENDER_DISTANCE.getValue() * 16)) {
                continue;
            }
            GL11.glPushMatrix();
            this.camera.setupObjectCamera(chunk.getKey().clipToWorldPosition());
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
            GLUtil.checkError("draw chunks:" + type);


            /*
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
             */
        }
    }


    //update
    private void updateChunks() {
        for (int i = 0; i < (this.setting.getValueAsInt("client.render.terrain.maxUpdate", 2)); i++) {
            if (this.updateService.getResultSize() > 0) {
                DrawCompile<RenderChunk> compile = this.updateService.getAvailableCompile();
                if (compile == null) {
                    continue;
                }
                RenderChunk chunk=compile.getObject();
                if(!chunk.getLifetimeCounter().isAllocated()){
                    chunk.allocate();
                }
                compile.draw();
            }
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
                continue;
            }
        }
    }

    public void checkCallList(RenderChunk chunk, RenderType type, KeyMap<RenderChunkPos, RenderChunk> callList) {
        if (chunk.anyLayerFilled(type)) {
            if (!callList.contains(chunk.getKey())) {
                callList.add(chunk);
            }
        } else {
            callList.remove(chunk.getKey());
        }
    }

    public void checkChunkCache() {
        int dist = ClientSettingRegistry.CHUNK_RENDER_DISTANCE.getValue();

        if(this.updateService==null){
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
         */

        long playerCX = (long) (this.camera.getPosition().x / 16);
        long playerCZ = (long) (this.camera.getPosition().z / 16);
        long playerCY = (long) (this.camera.getPosition().y / 16);
        for (long cx = playerCX - dist; cx <= playerCX + dist; cx++) {
            for (long cz = playerCZ - dist; cz <= playerCZ + dist; cz++) {
                long y1 = MathHelper.clamp(playerCY - dist, Chunk.HEIGHT / Chunk.WIDTH + 1, -1);
                long y2 = MathHelper.clamp(playerCY + dist, Chunk.HEIGHT / Chunk.WIDTH + 1, -1);
                for (long cy = y1; cy <= y2; cy++) {
                    RenderChunkPos pos = new RenderChunkPos(cx, cy, cz);
                    if (!this.camera.objectDistanceSmallerThan(pos.clipToWorldPosition(), dist * 16)) {
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
            if (this.camera.objectDistanceSmallerThan(c.getKey().clipToWorldPosition(), dist * 16)) {
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
            if (this.camera.objectDistanceSmallerThan(pos.clipToWorldPosition(), dist * 16)) {
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

    public void setUpdate(long x, long y, long z) {
        RenderChunkPos pos = new RenderChunkPos(x, y, z);
        RenderChunk chunk = this.chunks.get(pos);
        if (chunk == null) {
            chunk = new RenderChunk(this, this.world, pos);
        }
        this.updateService.startDrawing(chunk);
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

    public boolean chunkInDistance(RenderChunk chunk) {
        return this.camera.objectDistanceSmallerThan(chunk.getKey().clipToWorldPosition(), ClientSettingRegistry.CHUNK_RENDER_DISTANCE.getValue() * 16);
    }


}