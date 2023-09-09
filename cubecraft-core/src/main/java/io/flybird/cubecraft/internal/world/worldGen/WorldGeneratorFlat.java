package io.flybird.cubecraft.internal.world.worldGen;

import io.flybird.cubecraft.internal.block.BlockRegistry;
import io.flybird.cubecraft.internal.block.BlockType;
import io.flybird.cubecraft.internal.world.biome.BiomesRegistry;
import io.flybird.cubecraft.internal.world.WorldType;
import io.flybird.cubecraft.world.chunk.Chunk;
import io.flybird.cubecraft.world.chunk.WorldChunk;
import io.flybird.cubecraft.world.worldGen.*;

public class WorldGeneratorFlat implements IChunkGenerator {
    @WorldGenListener(stage = GenerateStage.TERRAIN, world = WorldType.OVERWORLD)
    public void generateChunkTerrain(WorldChunk chunk, WorldGeneratorSetting setting) {
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                double h=120;
                for (int y = 0; y < h; y++) {
                    if (y <= h) {
                        chunk.setBlockID(x,y,z, BlockRegistry.STONE.getID());
                    }
                }
            }
        }
    }

    @WorldGenListener(stage = GenerateStage.BIOME_SURFACE, world = WorldType.OVERWORLD)
    public void genBiome(WorldChunk chunk, WorldGeneratorSetting setting) {
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                double h=120;
               // BiomesRegistry.PLAINS.buildSurface(chunk, x, z, h, setting.seed());
            }
        }
    }
}
