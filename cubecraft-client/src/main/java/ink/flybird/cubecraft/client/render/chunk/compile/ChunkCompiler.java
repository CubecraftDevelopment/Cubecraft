package ink.flybird.cubecraft.client.render.chunk.compile;

import ink.flybird.cubecraft.client.ClientRenderContext;
import ink.flybird.cubecraft.client.render.LevelRenderer;
import ink.flybird.cubecraft.client.render.block.IBlockRenderer;
import ink.flybird.cubecraft.client.render.chunk.RenderChunkPos;
import ink.flybird.cubecraft.client.render.chunk.layer.ChunkLayer;
import ink.flybird.cubecraft.internal.block.BlockType;
import ink.flybird.cubecraft.world.IWorld;
import ink.flybird.cubecraft.world.access.ChunkLoadAccess;
import ink.flybird.cubecraft.world.block.access.IBlockAccess;
import ink.flybird.cubecraft.world.chunk.ChunkLoadTicket;
import ink.flybird.cubecraft.world.chunk.ChunkPos;
import ink.flybird.jflogger.ILogger;
import ink.flybird.jflogger.LogManager;
import ink.flybird.quantum3d_legacy.draw.VertexBuilder;
import ink.flybird.quantum3d_legacy.draw.VertexBuilderAllocator;

import java.util.Objects;
import java.util.stream.Stream;

public interface ChunkCompiler {
    ILogger LOGGER = LogManager.getLogger("chunk-layer-compiler");

    static ChunkCompileResult rebuild(String id, IWorld world, RenderChunkPos pos, ChunkLayer layer) {
        long x = pos.getX();
        long y = pos.getY();
        long z = pos.getZ();

        ChunkPos p = ChunkPos.create(x, z);
        ChunkLoadAccess.loadChunkRange(world, p, 1, ChunkLoadTicket.LOAD_DATA);
        ChunkLoadAccess.addChunkLockRange(world, p, 1, pos);

        long x0 = x * 16, x1 = x0 + 15;
        long y0 = y * 16, y1 = y0 + 15;
        long z0 = z * 16, z1 = z0 + 15;

        Stream<IBlockAccess> stream = world.getAllBlockInRange(x0, y0, z0, x1, y1, z1).stream();
        boolean fullAir = stream.allMatch(block -> Objects.equals(block.getBlockID(), BlockType.AIR));
        boolean solidAndNear = world.areaSolidAndNear(x0, y0, z0, x1, y1, z1);


        if (solidAndNear || fullAir) {
            ChunkLoadAccess.loadChunkRange(world, p, 1, ChunkLoadTicket.LOAD_DATA);
            ChunkLoadAccess.removeChunkLockRange(world, p, 1, pos);
            return ChunkCompileResult.failed(pos, id);
        }

        VertexBuilder builder = VertexBuilderAllocator.createByPrefer(131072);
        try {
            builder.begin();
            boolean filled = compileBlocks(id, world, pos, builder);
            builder.end();
            layer.setFilled(filled);

            ChunkLoadAccess.loadChunkRange(world, p, 1, ChunkLoadTicket.LOAD_DATA);
            ChunkLoadAccess.removeChunkLockRange(world, p, 1, pos);

            if (filled) {
                return ChunkCompileResult.success(pos, layer, builder);
            }

            builder.free();
            return ChunkCompileResult.failed(pos, id);
        } catch (Exception e) {
            LOGGER.error(e);

            builder.free();
            return ChunkCompileResult.failed(pos, id);
        }
    }

    static ChunkCompileResult build(String id, IWorld world, RenderChunkPos pos) {
        boolean vbo = LevelRenderer.SettingHolder.CHUNK_USE_VBO.getValue();
        ChunkLayer layer = ClientRenderContext.CHUNK_LAYER_RENDERER.create(id, vbo, pos);
        return rebuild(id, world, pos, layer);
    }

    static boolean compileBlocks(String layerID, IWorld world, RenderChunkPos pos, VertexBuilder builder) {
        builder.begin();

        long x = pos.getX();
        long y = pos.getY();
        long z = pos.getZ();
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
                    if (LevelRenderer.SettingHolder.CHUNK_FIX_DISTANCE.getValue()) {
                        renderer.renderBlock(blockAccess, layerID, world, cx, cy, cz, builder);
                        continue;
                    }
                    renderer.renderBlock(blockAccess, layerID, world, globalX, globalY, globalZ, builder);
                }
            }
        }
        builder.end();
        return builder.getCount() > 0;
    }
}
