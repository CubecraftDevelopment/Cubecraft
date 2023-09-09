package ink.flybird.cubecraft.client.internal.renderer.world.chunk;

import ink.flybird.cubecraft.client.ClientRenderContext;
import ink.flybird.quantum3d.Camera;
import ink.flybird.quantum3d.compile.CompileCallable;
import ink.flybird.quantum3d.draw.*;
import ink.flybird.quantum3d.drawcall.IRenderCall;
import ink.flybird.quantum3d.drawcall.ListRenderCall;
import ink.flybird.fcommon.container.KeyGetter;
import ink.flybird.fcommon.context.LifetimeCounter;
import ink.flybird.fcommon.math.AABB;
import ink.flybird.cubecraft.client.internal.renderer.world.TerrainRenderer;
import ink.flybird.cubecraft.client.render.DistanceComparable;
import ink.flybird.cubecraft.client.render.IRenderType;
import ink.flybird.cubecraft.client.render.renderer.IBlockRenderer;
import io.flybird.cubecraft.internal.block.BlockType;
import io.flybird.cubecraft.world.IWorld;
import io.flybird.cubecraft.world.access.ChunkLoadAccess;
import io.flybird.cubecraft.world.block.IBlockAccess;
import io.flybird.cubecraft.world.chunk.ChunkLoadTicket;
import io.flybird.cubecraft.world.chunk.ChunkPos;
import io.flybird.cubecraft.world.entity.Entity;
import org.joml.Vector3d;

import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

//todo:区域渲染（整体渲染，无矩阵变换）
//todo:多个透明渲染层冲突
public final class RenderChunk implements KeyGetter<RenderChunkPos>, DistanceComparable, CompileCallable<RenderChunk> {
    public final RenderChunkPos pos;
    public final IWorld world;
    private final TerrainRenderer parent;
    private final Map<String, ChunkLayerRenderer> layers;
    private final IRenderCall visibleAreaRenderCall;
    private final LifetimeCounter lifetimeCounter = new LifetimeCounter();

    //todo:区块数据通道化
    //todo:完善界面，完善组件
    //
    //todo:队列异常大小

    public RenderChunk(TerrainRenderer parent, IWorld world, RenderChunkPos pos) {
        this.parent = parent;
        boolean vbo = false;
        this.layers = ClientRenderContext.CHUNK_LAYER_RENDERER.createAll(vbo);
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

        for (ChunkLayerRenderer layer : this.layers.values()) {
            layer.render(type, this);
        }
    }

    public IRenderCall getVisibleAreaCall() {
        return this.visibleAreaRenderCall;
    }

    public boolean anyLayerFilled(IRenderType type) {
        for (ChunkLayerRenderer container : this.layers.values()) {
            if (container.getRenderType() != type) {
                continue;
            }
            if (container.isFilled()) {
                return true;
            }
        }
        return false;
    }


    @Override
    public Set<IDrawCompile<RenderChunk>> compile() {
        Set<IDrawCompile<RenderChunk>> result = new HashSet<>();

        long x = this.pos.getX();
        long y = this.pos.getY();
        long z = this.pos.getZ();

        ChunkPos pos = new ChunkPos(x, z);
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


        for (ChunkLayerRenderer layer : this.layers.values()) {
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

    private IDrawCompile<RenderChunk> compile(ChunkLayerRenderer layer) {
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
                    if (TerrainRenderer.DISTANCE_FIX) {
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
        for (ChunkLayerRenderer container : this.layers.values()) {
            container.allocate();
        }
    }

    public void destroy() {
        if(!this.lifetimeCounter.isAllocated()){
            return;
        }
        this.visibleAreaRenderCall.free();
        for (ChunkLayerRenderer container : this.layers.values()) {
            container.destroy();
        }
        this.lifetimeCounter.release();
    }

    @Override
    public boolean shouldCompile() {
        return this.parent.chunkInDistance(this);
    }

    public AABB getVisibleArea(Camera camera) {
        return RenderChunkPos.getAABBFromPos(this.getKey(), camera);
    }

    @Override
    public String toString() {
        return this.getKey().toString();
    }

    public LifetimeCounter getLifetimeCounter() {
        return lifetimeCounter;
    }
}