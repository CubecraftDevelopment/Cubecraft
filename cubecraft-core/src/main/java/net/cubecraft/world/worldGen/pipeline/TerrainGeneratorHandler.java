package net.cubecraft.world.worldGen.pipeline;

import net.cubecraft.world.chunk.PrimerChunk;

public interface TerrainGeneratorHandler {
    void generate(PrimerChunk chunk, PipelineContext context, ChunkContext chunkContext);
}
