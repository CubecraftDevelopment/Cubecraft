package net.cubecraft.client.render.chunk.compile;

import ink.flybird.quantum3d_legacy.draw.DrawMode;
import ink.flybird.quantum3d_legacy.draw.OffHeapVertexBuilder;
import ink.flybird.quantum3d_legacy.draw.VertexBuilder;
import net.cubecraft.client.context.ClientRenderContext;
import net.cubecraft.client.render.block.IBlockRenderer;
import net.cubecraft.client.render.chunk.RenderChunkPos;
import net.cubecraft.internal.block.BlockType;
import net.cubecraft.world.World;
import net.cubecraft.world.block.access.IBlockAccess;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;

public interface ChunkCompiler {
    Logger LOGGER = LogManager.getLogger("ChunkCompiler");

    Map<String, VertexBuilder[]> BUILDERS = new HashMap<>();


    static VertexBuilder[] createStack() {
        VertexBuilder[] builders = new VertexBuilder[7];

        for (int i = 0; i < builders.length; i++) {
            builders[i] = new OffHeapVertexBuilder(16384, DrawMode.QUADS);
        }

        return builders;
    }

    static void build(Queue<ChunkCompileResult> callback, World world, RenderChunkPos pos, ChunkLayerCompilation[] compilations) {
        ChunkCompileRegion region;
        try {
            region = ChunkCompileRegion.read(world, pos);
        } catch (Throwable t) {
            LOGGER.warn("failed to compile chunk at {}", pos);
            LOGGER.throwing(t);
            return;
        }

        if (region == null) {
            for (ChunkLayerCompilation compilation : compilations) {
                callback.add(ChunkCompileResult.failed(pos, compilation.getLayerId()));
            }
            return;
        }

        for (ChunkLayerCompilation compilation : compilations) {
            var id = compilation.getLayerId();
            var layer = compilation.getLayer();


            String mid = Thread.currentThread().getName() + id;

            if (!BUILDERS.containsKey(mid)) {
                BUILDERS.put(mid, createStack());
            }

            VertexBuilder[] builders = BUILDERS.get(mid);

            try {
                var filled = compileBlocks(region, id, pos, builders);
                layer.setFilled(filled);

                if (filled) {
                    BUILDERS.put(mid, createStack());
                    callback.add(ChunkCompileResult.success(pos, layer, builders));
                    continue;
                }

                callback.add(ChunkCompileResult.failed(pos, id));
            } catch (Exception e) {
                LOGGER.throwing(e);
                callback.add(ChunkCompileResult.failed(pos, id));
            }
        }
    }


    static boolean compileBlocks(ChunkCompileRegion region, String layer, RenderChunkPos pos, VertexBuilder[] builder) {
        for (VertexBuilder b : builder) {
            b.begin();
        }

        long x = pos.getX();
        long y = pos.getY();
        long z = pos.getZ();


        for (int cx = 0; cx < 16; ++cx) {
            for (int cz = 0; cz < 16; ++cz) {
                for (int cy = 0; cy < 16; ++cy) {
                    IBlockAccess block = region.getBlockAccess(x * 16 + cx, y * 16 + cy, z * 16 + cz);
                    if (Objects.equals(block.getBlockID(), BlockType.AIR)) {
                        continue;
                    }
                    IBlockRenderer renderer = ClientRenderContext.BLOCK_RENDERERS.get(block.getBlockId());

                    if (renderer == null) {
                        continue;
                    }

                    for (int face = 0; face < 7; face++) {
                        renderer.renderBlock(block, layer, region, face, cx, cy, cz, builder[face]);
                    }
                }
            }
        }

        boolean valid = false;

        for (VertexBuilder b : builder) {
            b.end();
            if (b.getCount() > 0) {
                valid = true;
            }
        }

        return valid;
    }
}
