package net.cubecraft.client.render.chunk.compile;

import net.cubecraft.client.ClientRenderContext;
import net.cubecraft.client.ClientSettingRegistry;
import net.cubecraft.client.render.block.IBlockRenderer;
import net.cubecraft.client.render.chunk.RenderChunkPos;
import net.cubecraft.client.render.chunk.layer.ChunkLayer;
import net.cubecraft.internal.block.BlockType;
import net.cubecraft.world.IWorld;
import net.cubecraft.world.access.ChunkLoadAccess;
import net.cubecraft.world.block.access.IBlockAccess;
import net.cubecraft.world.block.property.BlockPropertyDispatcher;
import net.cubecraft.world.chunk.pos.ChunkPos;
import ink.flybird.jflogger.ILogger;
import ink.flybird.jflogger.LogManager;
import ink.flybird.quantum3d_legacy.draw.DrawMode;
import ink.flybird.quantum3d_legacy.draw.OffHeapVertexBuilder;
import ink.flybird.quantum3d_legacy.draw.VertexBuilder;

import java.util.Objects;
import java.util.stream.Stream;

public interface ChunkCompiler {
    ILogger LOGGER = LogManager.getLogger("chunk-layer-compiler");

    static ChunkCompileResult rebuild(String id, IWorld world, RenderChunkPos pos, ChunkLayer layer) {
        long x = pos.getX();
        long y = pos.getY();
        long z = pos.getZ();

        ChunkPos p = ChunkPos.create(x, z);
        ChunkLoadAccess.addChunkLockRange(world, p, 1, pos);

        long x0 = x * 16, x1 = x0 + 15;
        long y0 = y * 16, y1 = y0 + 15;
        long z0 = z * 16, z1 = z0 + 15;

        Stream<IBlockAccess> stream = world.getAllBlockInRange(x0, y0, z0, x1, y1, z1).stream();

        if (stream.allMatch(block -> Objects.equals(block.getBlockID(), BlockType.AIR))) {
            ChunkLoadAccess.removeChunkLockRange(world, p, 1, pos);
            return ChunkCompileResult.failed(pos, id);
        }
        if(world.getAllBlockInRange(x0-1, y0-1, z0-1, x1+1, y1+1, z1+1).stream().allMatch(BlockPropertyDispatcher::isSolid)){
            ChunkLoadAccess.removeChunkLockRange(world, p, 1, pos);
            return ChunkCompileResult.failed(pos, id);
        }

        VertexBuilder builder = new OffHeapVertexBuilder(131072, DrawMode.QUADS);
        try {
            builder.begin();
            boolean filled = compileBlocks(id, world, pos, builder);
            builder.end();
            layer.setFilled(filled);
            ChunkLoadAccess.removeChunkLockRange(world, p, 1, pos);

            if (filled) {
                return ChunkCompileResult.success(pos, layer, builder);
            }

            builder.free();
            return ChunkCompileResult.failed(pos, id);
        } catch (Exception e) {
            LOGGER.error(e);
            builder.free();
            ChunkLoadAccess.removeChunkLockRange(world, p, 1, pos);
            return ChunkCompileResult.failed(pos, id);
        }
    }

    static ChunkCompileResult build(String id, IWorld world, RenderChunkPos pos) {
        boolean vbo = ClientSettingRegistry.CHUNK_USE_VBO.getValue();
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
                    if (ClientSettingRegistry.CHUNK_FIX_DISTANCE.getValue()) {
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
