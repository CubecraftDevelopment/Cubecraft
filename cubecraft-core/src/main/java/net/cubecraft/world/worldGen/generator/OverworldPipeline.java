package net.cubecraft.world.worldGen.generator;

import net.cubecraft.world.block.blocks.BlockRegistry;
import net.cubecraft.internal.block.BlockType;
import net.cubecraft.world.chunk.Chunk;
import net.cubecraft.world.chunk.pos.ChunkPos;
import net.cubecraft.world.chunk.ProviderChunk;
import net.cubecraft.world.worldGen.cache.HeightMap;
import net.cubecraft.world.worldGen.noiseGenerator.Noise;
import net.cubecraft.world.worldGen.noiseGenerator.PerlinNoise;
import net.cubecraft.world.worldGen.pipeline.ChunkGenerateContext;
import net.cubecraft.world.worldGen.pipeline.ChunkGeneratorPipeline;
import net.cubecraft.world.worldGen.pipeline.TerrainGeneratorHandler;
import net.cubecraft.world.worldGen.pipeline.WorldGenPipelineBuilder;
import ink.flybird.fcommon.registry.TypeItem;

import java.util.Random;

@TypeItem("cubecraft:overworld")
public final class OverworldPipeline implements WorldGenPipelineBuilder {
    @Override
    public ChunkGeneratorPipeline build(String worldType, long seed) {
        return new ChunkGeneratorPipeline(seed).addLast(new NoiseBuilder()).addLast(new TerrainShaper()).addLast(new SurfaceBuilder());
    }

    @TypeItem("cubecraft:overworld/noise_builder")
    static final class NoiseBuilder implements TerrainGeneratorHandler {
        public static final String CONTINENTAL_NOISE_ID = "cubecraft:overworld/continental";
        public static final String EROSION_NOISE_ID = "cubecraft:overworld/erosion";
        public static final String HEIGHTMAP_ID = "cubecraft:overworld/main_heightmap";

        @Override
        public void generate(ChunkGenerateContext context) {
            long seed = context.getSeed();
            ChunkPos p = context.getChunk().getKey();

            Noise noise2 = context.createNoise(CONTINENTAL_NOISE_ID, new PerlinNoise(new Random(seed & 976672604323801166L), 3));
            Noise noise = context.createNoise(EROSION_NOISE_ID, new PerlinNoise(new Random(seed & 27384032781293743L), 6));

            HeightMap heightMap = context.createHeightMap(HEIGHTMAP_ID);
            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    double v = 2 * noise2.getValue(p.toWorldPosX(x) / 512d, p.toWorldPosZ(z) / 512d) / 6d;
                    double vv = 242.41 * v * v * v * v + 49.302 * v * v * v - 137.228 * v * v + 160.18 * v + 137.35;
                    double h1 = noise.getValue(p.toWorldPosX(x) / 4d, p.toWorldPosZ(z) / 4d) * 4f;

                    double h = vv*1.105 + (v) * h1;
                    heightMap.setValue(x, z, h);
                }
            }
        }
    }

    @TypeItem("cubecraft:overworld/terrain_shaper")
    static final class TerrainShaper implements TerrainGeneratorHandler {

        @Override
        public void generate(ChunkGenerateContext context) {
            ProviderChunk chunk = context.getChunk();
            HeightMap heightMap = context.getHeightMap(NoiseBuilder.HEIGHTMAP_ID);

            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    int h = heightMap.sample(x, z);
                    for (int y = 0; y < Chunk.HEIGHT; y++) {
                        if (y <= h) {
                            chunk.setBlockID(x, y, z, BlockType.STONE);
                            continue;
                        }
                        if (y < 128.0) {
                            chunk.setBlockID(x, y, z, BlockRegistry.CALM_WATER.getID());
                        }
                    }
                }
            }
        }
    }

    @TypeItem("cubecraft:overworld/surface_builder")
    static final class SurfaceBuilder implements TerrainGeneratorHandler {
        @Override
        public void generate(ChunkGenerateContext context) {
            ProviderChunk chunk = context.getChunk();
            HeightMap heightMap = context.getHeightMap(NoiseBuilder.HEIGHTMAP_ID);

            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    int h = heightMap.sample(x, z);
                    if (h < 0 || h >= Chunk.HEIGHT) {
                        continue;
                    }
                    if (h >= 128.0&&h<350) {
                        chunk.setBlockID(x, h, z, BlockType.GRASS_BLOCK);
                        chunk.setBlockID(x, h - 1, z, BlockType.DIRT);
                        chunk.setBlockID(x, h - 2, z, BlockType.DIRT);
                    }
                }
            }
        }
    }
}