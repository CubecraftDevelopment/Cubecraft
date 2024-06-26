package net.cubecraft.client.render.chunk.compile;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import ink.flybird.quantum3d_legacy.draw.DrawMode;
import ink.flybird.quantum3d_legacy.draw.OffHeapVertexBuilder;
import ink.flybird.quantum3d_legacy.draw.VertexBuilder;
import net.cubecraft.client.ClientSettingRegistry;
import net.cubecraft.client.context.ClientRenderContext;
import net.cubecraft.client.render.block.IBlockRenderer;
import net.cubecraft.client.render.chunk.RenderChunkPos;
import net.cubecraft.client.render.chunk.layer.ChunkLayer;
import net.cubecraft.internal.block.BlockType;
import net.cubecraft.world.IWorld;
import net.cubecraft.world.access.ChunkLoadAccess;
import net.cubecraft.world.block.access.IBlockAccess;
import net.cubecraft.world.chunk.WorldChunk;
import net.cubecraft.world.chunk.pos.ChunkPos;
import net.cubecraft.world.chunk.task.ChunkLoadTicket;

import java.util.Objects;
import java.util.concurrent.ExecutionException;

public interface ChunkCompiler {
    Logger LOGGER = LogManager.getLogger("chunk-layer-compiler");

    static ChunkCompileResult rebuild(String id, IWorld world, RenderChunkPos pos, ChunkLayer layer) {
        ChunkPos p = ChunkPos.create(pos.getX(), pos.getZ());

        IBlockAccess[][][] compileRegion = generateCompileRegion(world, pos);

        if (compileRegion == null) {
            return ChunkCompileResult.failed(pos, id);
        }

        ChunkLoadAccess.addChunkLockRange(world, p, 1, pos);

        VertexBuilder builder = new OffHeapVertexBuilder(131072, DrawMode.QUADS);
        try {
            builder.begin();
            boolean filled = compileBlocks(compileRegion, id, world, pos, builder);
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

    static boolean compileBlocks(IBlockAccess[][][] compileRegion,String layerID, IWorld world, RenderChunkPos pos, VertexBuilder builder) {
        builder.begin();

        long x = pos.getX();
        long y = pos.getY();
        long z = pos.getZ();

        for (int cx = 0; cx < 16; ++cx) {
            for (int cz = 0; cz < 16; ++cz) {
                for (int cy = 0; cy < 16; ++cy) {
                    IBlockAccess blockAccess = compileRegion[cx][cy][cz];
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

    static IBlockAccess[][][] generateCompileRegion(IWorld world, RenderChunkPos pos) {
        long x = pos.getX();
        long y = pos.getY();
        long z = pos.getZ();
        ChunkPos p = ChunkPos.create(x, z);

        long x0 = x * 16, x1 = x0 + 15;
        long y0 = y * 16, y1 = y0 + 15;
        long z0 = z * 16, z1 = z0 + 15;

        WorldChunk c;
        try {
            c = (WorldChunk) world.loadChunk(p, ChunkLoadTicket.LOAD_DATA).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }

        IBlockAccess[][][] compileRegion = new IBlockAccess[16][16][16];
        c.getDataLock().addLock(pos);
        for (int cx = 0; cx < 16; ++cx) {
            for (int cz = 0; cz < 16; ++cz) {
                for (int cy = 0; cy < 16; ++cy) {
                    IBlockAccess block = c.getBlockAccess(x * 16 + cx, y * 16 + cy, z * 16 + cz);
                    compileRegion[cx][cy][cz] = block;
                }
            }
        }
        c.getDataLock().removeLock(pos);

        boolean discard = true;

        for (int cx = 0; cx < 16; ++cx) {
            for (int cz = 0; cz < 16; ++cz) {
                for (int cy = 0; cy < 16; ++cy) {
                    if (!compileRegion[cx][cy][cz].getBlockID().equals(BlockType.AIR)) {
                        discard = false;
                        break;
                    }
                }
            }
        }

        if (!discard) {
            ChunkLoadAccess.addChunkLockRange(world, p, 1, pos);
            return compileRegion;
        }
        return null;
    }
}
