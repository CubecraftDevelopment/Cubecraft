package net.cubecraft.client.render.chunk.compile;

import ink.flybird.jflogger.ILogger;
import ink.flybird.jflogger.LogManager;
import ink.flybird.quantum3d_legacy.draw.DrawMode;
import ink.flybird.quantum3d_legacy.draw.OffHeapVertexBuilder;
import ink.flybird.quantum3d_legacy.draw.VertexBuilder;
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
import net.cubecraft.world.chunk.Chunk;
import net.cubecraft.world.chunk.WorldChunk;
import net.cubecraft.world.chunk.pos.ChunkPos;
import net.cubecraft.world.chunk.task.ChunkLoadTicket;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

public interface ChunkCompiler {
    ILogger LOGGER = LogManager.getLogger("chunk-layer-compiler");

    static ChunkCompileResult rebuild(String id, IWorld world, RenderChunkPos pos, ChunkLayer layer) {
        ChunkPos p = ChunkPos.create(pos.getX(), pos.getZ());
        if (testForDiscard(world, pos)) {
            return ChunkCompileResult.failed(pos, id);
        }

        ChunkLoadAccess.addChunkLockRange(world, p, 1, pos);

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
                    renderer.renderBlock(blockAccess, layerID, world, cx, cy, cz, builder);
                }
            }
        }
        builder.end();
        return builder.getCount() > 0;
    }

    static boolean testForDiscard(IWorld world, RenderChunkPos pos) {
        long x = pos.getX();
        long y = pos.getY();
        long z = pos.getZ();
        ChunkPos p = ChunkPos.create(x, z);

        long x0 = x * 16, x1 = x0 + 15;
        long y0 = y * 16, y1 = y0 + 15;
        long z0 = z * 16, z1 = z0 + 15;

        boolean discard = true;
        WorldChunk c;
        try {
            c = (WorldChunk) world.loadChunk(p, ChunkLoadTicket.LOAD_DATA).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
        c.getDataLock().addLock(pos);
        IBlockAccess[] list = c.getAllBlockInRange(x0, y0, z0, x1, y1, z1);
        for (IBlockAccess access : list) {
            if (!Objects.equals(access.getBlockID(), BlockType.AIR)) {
                discard = false;
                break;
            }
        }
        if (discard) {
            c.getDataLock().removeLock(pos);
            return true;
        }


        discard = true;
        ChunkLoadAccess.addChunkLockRange(world, p, 1, pos);
        list = world.getAllBlockInRange(x0 - 1, y0 - 1, z0 - 1, x1 + 1, y1 + 1, z1 + 1);
        return false;
        /*
        for (IBlockAccess access : list) {
            if (!BlockPropertyDispatcher.isSolid(access)) {
                discard = false;
                break;
            }
        }
        if (discard) {
            ChunkLoadAccess.removeChunkLockRange(world, p, 1, pos);
            return true;
        }
        return false;

         */
    }
}
