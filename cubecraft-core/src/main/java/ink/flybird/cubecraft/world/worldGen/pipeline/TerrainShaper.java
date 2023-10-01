package ink.flybird.cubecraft.world.worldGen.pipeline;

import ink.flybird.cubecraft.world.worldGen.context.ChunkGenerateContext;

public interface TerrainShaper {

    void generateTerrain(ChunkGenerateContext context);
}
