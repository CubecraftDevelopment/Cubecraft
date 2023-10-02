package ink.flybird.cubecraft.internal.world.worldGen;

import ink.flybird.cubecraft.internal.block.BlockRegistry;
import ink.flybird.cubecraft.internal.block.BlockType;
import ink.flybird.cubecraft.internal.world.WorldType;
import ink.flybird.cubecraft.world.chunk.Chunk;
import ink.flybird.cubecraft.world.chunk.ChunkPos;
import ink.flybird.cubecraft.world.chunk.WorldChunk;
import ink.flybird.cubecraft.world.worldGen.GenerateStage;
import ink.flybird.cubecraft.world.worldGen.IChunkGenerator;
import ink.flybird.cubecraft.world.worldGen.WorldGenListener;
import ink.flybird.cubecraft.world.worldGen.WorldGeneratorSetting;
import ink.flybird.cubecraft.world.worldGen.noiseGenerator.Noise;
import ink.flybird.cubecraft.world.worldGen.noiseGenerator.PerlinNoise;
import ink.flybird.cubecraft.world.worldGen.noiseGenerator.SimplexNoise;
import ink.flybird.cubecraft.world.worldGen.templete.Modification;
import ink.flybird.cubecraft.world.worldGen.templete.Scale;
import ink.flybird.cubecraft.world.worldGen.templete.Select;

import java.util.Random;

public class WorldGeneratorOverworld implements IChunkGenerator {
    private Noise noise;
    private Noise noise2;

    public WorldGeneratorOverworld() {
        Noise altitudeHigh = new Modification(1 / 32f, 0, 2.4, 6, new PerlinNoise(new Random(523615), 3));
        Noise altitudeLow = new Modification(1 / 32f, 0, 2.4, -3, new PerlinNoise(new Random(849120), 4));
        this.noise = new Select(altitudeHigh, altitudeLow, new Scale(new PerlinNoise(new Random(114514), 4), 4d, 4d));
        this.noise = new PerlinNoise(new Random(12123568), 8);
        this.noise2 = new PerlinNoise(new Random(832193421),4);
    }

    @WorldGenListener(stage = GenerateStage.TERRAIN, world = WorldType.OVERWORLD)
    public void generateChunkTerrain(WorldChunk chunk, WorldGeneratorSetting setting) {
        ChunkPos p = chunk.getKey();

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {


                double v =2* noise2.getValue(p.toWorldPosX(x) / 512f, chunk.getKey().toWorldPosZ(z) / 512f)/6f;
                double vv = 242.41*v*v*v*v + 49.302*v*v*v - 137.228*v*v + 160.18*v + 137.35;

                double h1 = noise.getValue(p.toWorldPosX(x) / 4f, chunk.getKey().toWorldPosZ(z) / 4f) * 4f;

                double h = vv+(2*v*v)*h1;

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

    //@WorldGenListener(stage = GenerateStage.BIOME_SURFACE, world = WorldType.OVERWORLD)
    public void genBiome(WorldChunk chunk, WorldGeneratorSetting setting) {
        ChunkPos p = chunk.getKey();
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                double h = this.noise.getValue(p.toWorldPosX(x), p.toWorldPosZ(z)) / 2f + 120f;
                if (h < 128.0) {
                    continue;
                }
                // ContentRegistries.BIOME.getChunk(BiomeType.PLAINS).buildSurface(chunk, x, z, h, setting.seed());
            }
        }
    }
}
