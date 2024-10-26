package net.cubecraft.world.worldGen.pipeline.pipelines;

import me.gb2022.commons.registry.TypeItem;
import net.cubecraft.internal.block.BlockType;
import net.cubecraft.world.block.blocks.BlockRegistry;
import net.cubecraft.world.chunk.PrimerChunk;
import net.cubecraft.world.worldGen.pipeline.*;

@TypeItem("cubecraft:flat")
public final class FlatWorldPipeline implements WorldGenPipelineBuilder {
    @Override
    public ChunkGeneratorPipeline build(String worldType, long seed) {
        return new ChunkGeneratorPipeline(seed).beforeStructure().addLast(new TerrainBuilder()).addLast(new SurfaceBuilder()).pipe();
    }

    @TypeItem("cubecraft:flat/terrain_builder")
    static final class TerrainBuilder implements TerrainGeneratorHandler {

        @Override
        public void generate(PrimerChunk chunk, PipelineContext context, ChunkContext chunkContext) {
            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    for (int y = 0; y < 128; y++) {
                        chunk.setBlockID(x, y, z, BlockRegistry.STONE.getID());
                    }
                }
            }
        }
    }

    @TypeItem("cubecraft:flat/surface_builder")
    static final class SurfaceBuilder implements TerrainGeneratorHandler {
        @Override
        public void generate(PrimerChunk chunk, PipelineContext context, ChunkContext chunkContext) {

            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    chunk.setBlockID(x, 128, z, BlockType.GRASS_BLOCK);
                    chunk.setBlockID(x, 127, z, BlockType.DIRT);
                    chunk.setBlockID(x, 126, z, BlockType.DIRT);
                }
            }
        }
    }
}