package net.cubecraft.internal.world.worldGen;

import net.cubecraft.internal.world.WorldType;
import net.cubecraft.world.chunk.WorldChunk;
import net.cubecraft.world.worldGen.GenerateStage;
import net.cubecraft.world.worldGen.IChunkGenerator;
import net.cubecraft.world.worldGen.WorldGenListener;
import net.cubecraft.world.worldGen.WorldGeneratorSetting;
import net.cubecraft.world.worldGen.noiseGenerator.Noise;
import net.cubecraft.world.worldGen.noiseGenerator.PerlinNoise;
import net.cubecraft.world.worldGen.templete.Modification;
import net.cubecraft.world.worldGen.templete.Scale;
import net.cubecraft.world.worldGen.templete.Select;

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
    }

    //@WorldGenListener(stage = GenerateStage.BIOME_SURFACE, world = WorldType.OVERWORLD)
    public void genBiome(WorldChunk chunk, WorldGeneratorSetting setting) {

    }
}
