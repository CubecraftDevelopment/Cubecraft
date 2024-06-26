package net.cubecraft.client.internal.renderer.world.chunk;

import net.cubecraft.client.ClientSettingRegistry;
import net.cubecraft.client.context.ClientRenderContext;
import net.cubecraft.client.render.RenderType;
import net.cubecraft.client.render.chunk.RenderChunkPos;
import net.cubecraft.client.render.chunk.layer.ChunkLayer;
import ink.flybird.quantum3d_legacy.drawcall.IRenderCall;
import ink.flybird.quantum3d_legacy.drawcall.ListRenderCall;
import me.gb2022.commons.container.KeyGetter;
import me.gb2022.commons.context.LifetimeCounter;
import net.cubecraft.client.render.chunk.ChunkRenderer;
import net.cubecraft.client.render.DistanceComparable;
import net.cubecraft.client.render.IRenderType;
import net.cubecraft.world.IWorld;
import net.cubecraft.world.entity.Entity;
import org.joml.Vector3d;

import java.util.Map;

//todo:多个透明渲染层冲突
//todo:区块数据通道化
//todo:完善界面，完善组件

public final class RenderChunk implements KeyGetter<RenderChunkPos>, DistanceComparable{
    public final RenderChunkPos pos;
    public final IWorld world;
    private final ChunkRenderer parent;
    private final Map<String, ChunkLayer> layers;
    private final IRenderCall visibleAreaRenderCall;
    private final LifetimeCounter lifetimeCounter = new LifetimeCounter();



    public RenderChunk(ChunkRenderer parent, IWorld world, RenderChunkPos pos) {
        this.parent = parent;
        this.layers = ClientRenderContext.CHUNK_LAYER_RENDERER.createAll(ClientSettingRegistry.CHUNK_USE_VBO.getValue());
        this.world = world;
        this.pos = pos;
        this.visibleAreaRenderCall = new ListRenderCall();
    }


    //render
    public void render(IRenderType type) {
        try {
            this.lifetimeCounter.check();
        }catch (Exception e){
            return;
        }

        for (ChunkLayer layer : this.layers.values()) {
            layer.render();
        }
    }

    public int getRenderLists(RenderType type){
        try {
            this.lifetimeCounter.check();
        }catch (Exception e){
            return -1;
        }

        for (ChunkLayer layer : this.layers.values()) {
            if(layer.isFilled()&&layer.getRenderType()==type) {
                return layer.getRenderCall().getHandle();
            }
        }
        return -1;
    }

    public IRenderCall getVisibleAreaCall() {
        return this.visibleAreaRenderCall;
    }

    public boolean anyLayerFilled(IRenderType type) {
        for (ChunkLayer container : this.layers.values()) {
            if (container.getRenderType() != type) {
                continue;
            }
            if (container.isFilled()) {
                return true;
            }
        }
        return false;
    }

/*
    @Override
    public Set<IDrawCompile<RenderChunk>> compile() {
        Set<IDrawCompile<RenderChunk>> result = new HashSet<>();

        long x = this.pos.getX();
        long y = this.pos.getY();
        long z = this.pos.getZ();

        ChunkPos pos = ChunkPos.create(x, z);
        ChunkLoadAccess.loadChunkRange(this.world, pos, 1, ChunkLoadTicket.LOAD_DATA);
        ChunkLoadAccess.addChunkLockRange(this.world, pos, 1, this);

        long x0 = x * 16, x1 = x0 + 15;
        long y0 = y * 16, y1 = y0 + 15;
        long z0 = z * 16, z1 = z0 + 15;

        Stream<IBlockAccess> stream = this.world.getAllBlockInRange(x0, y0, z0, x1, y1, z1).stream();
        boolean fullAir = stream.allMatch(block -> Objects.equals(block.getBlockID(), BlockType.AIR));
        boolean solidAndNear = this.world.areaSolidAndNear(x0, y0, z0, x1, y1, z1);

        if (solidAndNear || fullAir) {
            ChunkLoadAccess.loadChunkRange(this.world, pos, 1, ChunkLoadTicket.LOAD_DATA);
            ChunkLoadAccess.removeChunkLockRange(this.world, pos, 1, this);
            return result;
        }


        for (ChunkLayer layer : this.layers.values()) {
            IDrawCompile<RenderChunk> cResult = this.compile(layer);
            if (cResult == null) {
                continue;
            }
            result.add(cResult);
        }

        if (result.isEmpty()) {
            result.add(new EmptyDrawCompile<>(this));
        }

        ChunkLoadAccess.loadChunkRange(this.world, pos, 1, ChunkLoadTicket.LOAD_DATA);
        ChunkLoadAccess.removeChunkLockRange(this.world, pos, 1, this);

        return result;
    }

    private IDrawCompile<RenderChunk> compile(ChunkLayer layer) {
        VertexBuilder builder = VertexBuilderAllocator.createByPrefer(32768);
        try {
            builder.begin();
            boolean filled = this.compileBlocks(layer.getID(), builder);
            builder.end();
            layer.setFilled(filled);
            if (filled) {
                return new DrawCompile<>(layer.getRenderCall(), builder, this);
            }
            builder.free();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            builder.free();
            return null;
        }
    }

    private boolean compileBlocks(String layerID, VertexBuilder builder) {
        builder.begin();

        long x = this.pos.getX();
        long y = this.pos.getY();
        long z = this.pos.getZ();
        for (long cx = 0; cx < 16; ++cx) {
            for (long cz = 0; cz < 16; ++cz) {
                for (long cy = 0; cy < 16; ++cy) {
                    long globalX = cx + x * 16;
                    long globalY = cy + y * 16;
                    long globalZ = cz + z * 16;

                    IBlockAccess blockAccess = world.getBlockAccess(globalX, globalY, globalZ);
                    if (Objects.equals(blockAccess.getBlockID(), BlockType.AIR)) {
                        continue;
                    }
                    IBlockRenderer renderer = ClientRenderContext.BLOCK_RENDERER.get(blockAccess.getBlockID());
                    if (renderer == null) {
                        continue;
                    }
                    if (ClientSettingRegistry.CHUNK_FIX_DISTANCE.getValue()) {
                        renderer.renderBlock(blockAccess, layerID, this.world, cx, cy, cz, builder);
                        continue;
                    }
                    renderer.renderBlock(blockAccess, layerID, this.world, globalX, globalY, globalZ, builder);
                }
            }
        }
        builder.end();
        return builder.getCount() > 0;
    }


 */

    @Override
    public RenderChunkPos getKey() {
        return this.pos;
    }

    @Override
    public double distanceTo(Entity target) {
        return new Vector3d(
                this.getKey().getWorldX(),
                this.getKey().getWorldY(),
                this.getKey().getWorldZ()
        ).add(8, 8, 8).distance(target.getPosition());
    }

    public void allocate() {
        this.lifetimeCounter.allocate();
        this.visibleAreaRenderCall.allocate();
        for (ChunkLayer container : this.layers.values()) {
            container.allocate();
        }
    }

    public void destroy() {
        if(!this.lifetimeCounter.isAllocated()){
            return;
        }
        this.visibleAreaRenderCall.free();
        for (ChunkLayer container : this.layers.values()) {
            container.destroy();
        }
        this.lifetimeCounter.release();
    }

    @Override
    public String toString() {
        return this.getKey().toString();
    }

    public LifetimeCounter getLifetimeCounter() {
        return lifetimeCounter;
    }
}