package net.cubecraft.world.worldGen.pipeline.pipelines;

import com.terraforged.noise.source.Builder;
import com.terraforged.noise.source.RidgeNoise;
import me.gb2022.commons.math.MathHelper;
import me.gb2022.commons.registry.TypeItem;
import net.cubecraft.world.block.blocks.Blocks;
import net.cubecraft.world.chunk.Chunk;
import net.cubecraft.world.chunk.PrimerChunk;
import net.cubecraft.world.worldGen.noise.TFNoise;
import net.cubecraft.world.worldGen.pipeline.*;
import net.cubecraft.world.worldGen.pipeline.cache.HeightMap;
import net.cubecraft.world.worldGen.structure.generators.SmallTreeGenerator;
import net.cubecraft.world.worldGen.structure.shapes.SmallTree;

@TypeItem("cubecraft:overworld")
public final class SingleNoisePipeline implements WorldGenPipelineBuilder {

    @Override
    public ChunkGeneratorPipeline build(String worldType, long seed) {
        return new ChunkGeneratorPipeline(seed).beforeStructure()
                .addLast(new NoiseBuilder())
                .addLast(new TerrainShaper())
                .addLast(new SurfaceBuilder())
                .pipe();
    }

    @TypeItem("cubecraft:overworld/noise_builder")
    static final class NoiseBuilder implements TerrainGeneratorHandler {

        @Override
        public void generate(PrimerChunk chunk, PipelineContext context, ChunkContext chunkContext) {
            var seed = context.getSeed();
            var p = chunk.getKey();

            var continental = context.createNoise("test", TFNoise.wrap(seed & 483190132, new RidgeNoise(new Builder().octaves(2))));

            var heightMap = chunkContext.createHeightMap("main");

            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    var wx = p.toWorldPosX(x);
                    var wz = p.toWorldPosZ(z);


                    var c1 = continental.getValue(seed, wx / 800d, wz / 800d) + 1;

                    var c = c1*c1*c1*40;
                    heightMap.setValue(x, z, MathHelper.clamp(c+140, 16, Chunk.HEIGHT));
                }
            }
        }
    }

    @TypeItem("cubecraft:overworld/terrain_shaper")
    static final class TerrainShaper implements TerrainGeneratorHandler {

        @Override
        public void generate(PrimerChunk chunk, PipelineContext context, ChunkContext chunkContext) {
            HeightMap heightMap = chunkContext.getHeightMap("main");

            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    int h = heightMap.sample(x, z);
                    for (int y = 0; y < Chunk.HEIGHT; y++) {
                        if (y <= h) {
                            chunk.setBlockId(x, y, z, Blocks.STONE.getId());
                            continue;
                        }
                        if (y < 128.0) {
                            chunk.setBlockId(x, y, z, Blocks.WATER.getId());
                        }
                    }
                }
            }
        }
    }

    @TypeItem("cubecraft:overworld/surface_builder")
    static final class SurfaceBuilder implements TerrainGeneratorHandler {

        public void applyNormal(Chunk chunk, int x, int z, int h) {
            chunk.setBlockId(x, h, z, Blocks.GRASS_BLOCK.getId());
            chunk.setBlockId(x, h - 1, z, Blocks.DIRT.getId());
            chunk.setBlockId(x, h - 2, z, Blocks.DIRT.getId());
        }

        public void applyDesert(Chunk chunk, int x, int z, int h) {
            chunk.setBlockId(x, h, z, Blocks.SAND.getId());
            chunk.setBlockId(x, h - 1, z, Blocks.SAND.getId());
            chunk.setBlockId(x, h - 2, z, Blocks.SAND.getId());
        }

        public void applyCold(Chunk chunk, int x, int z, int h) {
            chunk.setBlockId(x, h, z, Blocks.SNOW.getId());
            chunk.setBlockId(x, h - 1, z, Blocks.SNOW.getId());
            chunk.setBlockId(x, h - 2, z, Blocks.ICE.getId());
            chunk.setBlockId(x, h - 3, z, Blocks.ICE.getId());
            chunk.setBlockId(x, h - 4, z, Blocks.ICE.getId());
        }

        @Override
        public void generate(PrimerChunk chunk, PipelineContext context, ChunkContext chunkContext) {
            var heightMap = chunkContext.getHeightMap("main");

            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    int h = heightMap.sample(x, z);
                    if (h < 0 || h >= Chunk.HEIGHT) {
                        continue;
                    }

                    if (h > 400) {
                        applyCold(chunk, x, z, h);
                        continue;
                    }


                    if (h >= 92.0 && h < 130) {
                        chunk.setBlockId(x, h, z, Blocks.SAND.getId());
                        continue;
                    }
                    if (h >= 32 && h < 92) {
                        chunk.setBlockId(x, h, z, Blocks.GRAVEL.getId());
                        continue;
                    }

                    applyNormal(chunk, x, z, h);
                    continue;
                }
            }
        }
    }
}