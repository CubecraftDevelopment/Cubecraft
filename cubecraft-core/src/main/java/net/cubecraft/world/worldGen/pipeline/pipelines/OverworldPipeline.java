package net.cubecraft.world.worldGen.pipeline.pipelines;

import com.terraforged.noise.source.Builder;
import com.terraforged.noise.source.SimplexNoise2;
import me.gb2022.commons.math.MathHelper;
import me.gb2022.commons.registry.TypeItem;
import net.cubecraft.util.Spline;
import net.cubecraft.world.block.blocks.Blocks;
import net.cubecraft.world.chunk.Chunk;
import net.cubecraft.world.chunk.PrimerChunk;
import net.cubecraft.world.worldGen.noise.Noise;
import net.cubecraft.world.worldGen.noise.PerlinNoise;
import net.cubecraft.world.worldGen.noise.SimplexNoise;
import net.cubecraft.world.worldGen.noise.TFNoise;
import net.cubecraft.world.worldGen.pipeline.*;
import net.cubecraft.world.worldGen.pipeline.cache.HeightMap;
import net.cubecraft.world.worldGen.structure.generators.SmallTreeGenerator;
import net.cubecraft.world.worldGen.structure.shapes.SmallTree;
import org.joml.Vector2d;

import java.util.Random;

@TypeItem("cubecraft:overworld")
public final class OverworldPipeline implements WorldGenPipelineBuilder {
    public static final String CONTINENTAL_NOISE = "cubecraft:overworld/continental";
    public static final String EROSION_NOISE = "cubecraft:overworld/erosion";
    public static final String TEMPERATURE_NOISE = "cubecraft:overworld/temperature";
    public static final String HUMIDITY_NOISE = "cubecraft:overworld/humidity";
    public static final String PV_NOISE = "cubecraft:overworld/pv";

    public static final String MAIN_HEIGHTMAP = "cubecraft:overworld/main";


    @Override
    public ChunkGeneratorPipeline build(String worldType, long seed) {
        return new ChunkGeneratorPipeline(seed).beforeStructure()
                .addLast(new NoiseBuilder())
                .addLast(new TerrainShaper())
                .addLast(new SurfaceBuilder())
                .pipe()
                .addStructure(new SmallTreeGenerator(new SmallTree(Blocks.OAK_LOG, Blocks.OAK_LEAVES)));
    }

    @TypeItem("cubecraft:overworld/noise_builder")
    static final class NoiseBuilder implements TerrainGeneratorHandler {
        public static final String EROSION_SHAPE_NOISE = "cubecraft:overworld/erosion_shape";

        public static final Spline HEIGHT = new Spline(
                new Vector2d(0, 0.06),
                new Vector2d(0.06, 0.09),
                new Vector2d(0.01, 0.2),
                new Vector2d(0.15, 0.25),
                new Vector2d(0.74, 0.26),
                new Vector2d(0.81, 0.255),
                new Vector2d(0.84, 0.36),
                new Vector2d(0.98, 0.55),
                new Vector2d(0.99, 0.62),
                new Vector2d(0.993, 0.71),
                new Vector2d(1, 0.82)
        );

        public static final Spline EROSION = new Spline(
                new Vector2d(0, 0.01),
                new Vector2d(0.08, 0.03),
                new Vector2d(0.2, 0.05),
                new Vector2d(0.3, 0.15),
                new Vector2d(0.4, 0.25),
                new Vector2d(0.75, 0.42),
                new Vector2d(0.76, 0.55),
                new Vector2d(0.8, 0.63),
                new Vector2d(0.9, 0.71),
                new Vector2d(0.95, 1.2),
                new Vector2d(1, 1.2)
        );

        public static final Spline MOUNTAIN = new Spline(
                new Vector2d(0, 0.01),
                new Vector2d(0.4, 0.01),
                new Vector2d(0.75, 0.03),
                new Vector2d(0.76, 0.09),
                new Vector2d(0.8, 0.3),
                new Vector2d(0.9, 0.92),
                new Vector2d(0.95, 1.1),
                new Vector2d(1, 1.1)
        );

        @Override
        public void generate(PrimerChunk chunk, PipelineContext context, ChunkContext chunkContext) {
            var seed = context.getSeed();
            var p = chunk.getKey();


            var continental = context.createNoise(CONTINENTAL_NOISE, TFNoise.wrap(seed & 483190132, new SimplexNoise2(new Builder())));
            var erosion = context.createNoise(EROSION_NOISE, TFNoise.wrap(seed & 423111431, new SimplexNoise2(new Builder())));
            var erosionShape = context.createNoise(EROSION_SHAPE_NOISE, new PerlinNoise(new Random(seed | 74320917), 6));
            var peak = context.createNoise(PV_NOISE, new PerlinNoise(new Random(seed | 74320917), 6));

            var heightMap = chunkContext.createHeightMap(MAIN_HEIGHTMAP);

            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    var wx = p.toWorldPosX(x);
                    var wz = p.toWorldPosZ(z);

                    var c = continental.getValue(seed, wx / 12000d, wz / 12000d);

                    var continentalMask = HEIGHT.interpolate(c);
                    var erosionMask = EROSION.interpolate(c);
                    var erosionShapeValue = (erosionShape.getValue(wx / 5d, wz / 5d)) * 1.4f;

                    //erosionMask *= MathHelper.scale(continentalMask, 0.35, 1.4, 0, 1);


                    var terrain = continentalMask * Chunk.HEIGHT;
                    var mountainMod = MOUNTAIN.interpolate(c) * Math.max(0, peak.getValue(wx / 16d, wz / 16d) * 3);
                    var erosionMod = erosionMask * erosionShapeValue;

                    var h = terrain + mountainMod - erosionMod;

                    heightMap.setValue(x, z, MathHelper.clamp(h, 16, Chunk.HEIGHT));
                }
            }
        }
    }

    @TypeItem("cubecraft:overworld/terrain_shaper")
    static final class TerrainShaper implements TerrainGeneratorHandler {

        @Override
        public void generate(PrimerChunk chunk, PipelineContext context, ChunkContext chunkContext) {
            HeightMap heightMap = chunkContext.getHeightMap(MAIN_HEIGHTMAP);

            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    int h = heightMap.sample(x, z);
                    for (int y = 0; y < Chunk.HEIGHT; y++) {
                        if (y <= h) {
                            chunk.setBlockId(x, y, z, Blocks.STONE.getId());
                            continue;
                        }
                        if (y < 128.0) {
                            chunk.setBlockId(x, y, z, Blocks.CALM_WATER.getId());
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
            var seed = context.getSeed();
            var p = chunk.getKey();

            var temperature = context.createNoise(TEMPERATURE_NOISE, new SimplexNoise(new Random(seed & 784291041 | 1482903)));
            var humidity = context.createNoise(HUMIDITY_NOISE, new SimplexNoise(new Random(seed & 78109244213L)));
            var continental = context.getNoise(CONTINENTAL_NOISE, Noise.class);

            var heightMap = chunkContext.getHeightMap(MAIN_HEIGHTMAP);

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

                    double temp = temperature.getValue(p.toWorldPosX(x) / 12000f, p.toWorldPosZ(z) / 12000f) + 1;//-1-1
                    double humid = humidity.getValue(p.toWorldPosX(x) / 12000f, p.toWorldPosZ(z) / 12000f) + 1;

                    temp = MathHelper.clamp(temp + 1 - (double) h / 300, 0, 1);

                    //dry
                    if (humid < 0.2) {
                        if (temp < 0.23) {
                            applyCold(chunk, x, z, h);
                            continue;
                        }
                        if (temp < 0.85) {
                            applyNormal(chunk, x, z, h);
                            continue;
                        }
                        applyDesert(chunk, x, z, h);
                        continue;
                    }

                    //mountain
                    if (h > 360) {
                        if (humid < 0.4) {
                            continue;
                        }
                        applyCold(chunk, x, z, h);
                        continue;
                    }

                    //high-plains
                    if (h > 220) {
                        applyNormal(chunk, x, z, h);
                        continue;
                    }

                    //near-water
                    if (continental.getValue(p.toWorldPosX(x) / 6000d, p.toWorldPosZ(z) / 6000d) * 1.4f + 0.2 < 20) {
                        applyNormal(chunk, x, z, h);
                        continue;
                    }

                    //cold
                    if (temp < 0.2) {
                        if (humid > 0.6) {
                            applyNormal(chunk, x, z, h);
                            continue;
                        }
                        applyCold(chunk, x, z, h);
                        continue;
                    }

                    //normal
                    if (temp < 0.8) {
                        applyNormal(chunk, x, z, h);
                        continue;
                    }

                    //so fucking hot
                    applyDesert(chunk, x, z, h);
                }
            }
        }
    }
}