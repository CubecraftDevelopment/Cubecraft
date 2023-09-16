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
import ink.flybird.cubecraft.world.worldGen.noiseGenerator.PerlinNoise;
import ink.flybird.cubecraft.world.worldGen.noiseGenerator.Synth;
import ink.flybird.cubecraft.world.worldGen.templete.Modification;
import ink.flybird.cubecraft.world.worldGen.templete.Scale;
import ink.flybird.cubecraft.world.worldGen.templete.Select;

import java.util.Random;

public class WorldGeneratorOverworld implements IChunkGenerator {
    private Synth noise;

    public WorldGeneratorOverworld() {
        Synth altitudeHigh = new Modification(1 / 32f, 0, 2.4, 6, new PerlinNoise(new Random(523615), 3));
        Synth altitudeLow = new Modification(1 / 32f, 0, 2.4, -3, new PerlinNoise(new Random(849120), 4));
        this.noise = new Select(altitudeHigh, altitudeLow, new Scale(new PerlinNoise(new Random(114514), 4), 4d, 4d));
        this.noise=new PerlinNoise(new Random(12123568),8);
    }

    @WorldGenListener(stage = GenerateStage.TERRAIN, world = WorldType.OVERWORLD)
    public void generateChunkTerrain(WorldChunk chunk, WorldGeneratorSetting setting) {
        ChunkPos p = chunk.getKey();

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {


                double h = noise.getValue(p.toWorldPosX(x)/4f,chunk.getKey().toWorldPosZ(z)/4f)+128;
                for (int y = 0; y < Chunk.HEIGHT; y++) {
                    if (y <= h) {
                        chunk.setBlockID(x, y, z, BlockType.STONE);
                        continue;
                    }
                    if (y <= 128.0) {
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
                // ContentRegistries.BIOME.get(BiomeType.PLAINS).buildSurface(chunk, x, z, h, setting.seed());
            }
        }
    }
}
