package io.flybird.cubecraft.internal.world.worldGen;

import io.flybird.cubecraft.internal.block.BlockRegistry;
import io.flybird.cubecraft.internal.block.BlockType;
import io.flybird.cubecraft.internal.world.WorldType;
import io.flybird.cubecraft.internal.world.biome.BiomeType;
import io.flybird.cubecraft.register.ContentRegistries;
import io.flybird.cubecraft.world.chunk.Chunk;
import io.flybird.cubecraft.world.chunk.WorldChunk;
import io.flybird.cubecraft.world.chunk.ChunkPos;
import io.flybird.cubecraft.world.worldGen.WorldGenListener;
import io.flybird.cubecraft.world.worldGen.WorldGeneratorSetting;
import io.flybird.cubecraft.world.worldGen.noiseGenerator.PerlinNoise;
import io.flybird.cubecraft.world.worldGen.noiseGenerator.Synth;
import io.flybird.cubecraft.world.worldGen.GenerateStage;
import io.flybird.cubecraft.world.worldGen.IChunkGenerator;
import io.flybird.cubecraft.world.worldGen.templete.Modification;
import io.flybird.cubecraft.world.worldGen.templete.Scale;
import io.flybird.cubecraft.world.worldGen.templete.Select;

import java.util.Random;

public class WorldGeneratorOverworld implements IChunkGenerator {
    private Synth noise;

    @WorldGenListener(stage = GenerateStage.BASE,world = WorldType.OVERWORLD)
    public void generateChunkBase(WorldChunk chunk, WorldGeneratorSetting setting) {
        Synth altitudeHigh = new Modification(1 / 32f, 0, 2.4, 6, new PerlinNoise(new Random(setting.seed()), 3));
        Synth altitudeLow = new Modification(1 / 32f, 0, 2.4, -3, new PerlinNoise(new Random(setting.seed() & 42378651025L - 4678908571L | 324684367), 4));
        this.noise = new Select(altitudeHigh, altitudeLow,new Scale(new PerlinNoise(new Random(setting.seed()%738210^1327899),4),4d,4d));
        this.noise=new Scale(new PerlinNoise(new Random(setting.seed()),10),4d,4d);
    }

    @WorldGenListener(stage = GenerateStage.TERRAIN, world = WorldType.OVERWORLD)
    public void generateChunkTerrain(WorldChunk chunk, WorldGeneratorSetting setting) {
        ChunkPos p = chunk.getKey();

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                double h=this.noise.getValue(p.toWorldPosX(x), p.toWorldPosZ(z))/2f+120f;
                for (int y = 0; y < Chunk.HEIGHT; y++) {
                    if (y <= h) {
                        chunk.setBlockID(x,y,z, BlockType.STONE);
                        continue;
                    }
                    if(y<=128.0){
                        chunk.setBlockID(x,y,z, BlockRegistry.CALM_WATER.getID());
                    }
                }
            }
        }
    }

    @WorldGenListener(stage = GenerateStage.BIOME_SURFACE, world = WorldType.OVERWORLD)
    public void genBiome(WorldChunk chunk, WorldGeneratorSetting setting) {
        ChunkPos p=chunk.getKey();
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                double h=this.noise.getValue(p.toWorldPosX(x), p.toWorldPosZ(z))/2f+120f;
                if(h<128.0){
                    continue;
                }
                ContentRegistries.BIOME.get(BiomeType.PLAINS).buildSurface(chunk, x, z, h, setting.seed());
            }
        }
    }
}
