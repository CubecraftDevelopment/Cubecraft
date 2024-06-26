package net.cubecraft.world.worldGen.generator;

import net.cubecraft.world.block.blocks.BlockRegistry;
import net.cubecraft.internal.block.BlockType;
import net.cubecraft.world.chunk.ProviderChunk;
import net.cubecraft.world.worldGen.pipeline.ChunkGenerateContext;
import net.cubecraft.world.worldGen.pipeline.ChunkGeneratorPipeline;
import net.cubecraft.world.worldGen.pipeline.WorldGenPipelineBuilder;
import net.cubecraft.world.worldGen.pipeline.TerrainGeneratorHandler;
import me.gb2022.commons.registry.TypeItem;

@TypeItem("cubecraft:flat")
public final class FlatWorldPipeline implements WorldGenPipelineBuilder {
    @Override
    public ChunkGeneratorPipeline build(String worldType, long seed) {
        return new ChunkGeneratorPipeline(seed).addLast(new TerrainBuilder()).addLast(new SurfaceBuilder());
    }

    @TypeItem("cubecraft:flat/terrain_builder")
    static final class TerrainBuilder implements TerrainGeneratorHandler {

        @Override
        public void generate(ChunkGenerateContext context) {
            ProviderChunk chunk = context.getChunk();

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
        public void generate(ChunkGenerateContext context) {
            ProviderChunk chunk = context.getChunk();

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