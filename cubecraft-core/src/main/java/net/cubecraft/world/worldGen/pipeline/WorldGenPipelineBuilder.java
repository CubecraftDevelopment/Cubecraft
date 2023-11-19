package net.cubecraft.world.worldGen.pipeline;

//todo:config
public interface WorldGenPipelineBuilder {
    ChunkGeneratorPipeline build(String worldType, long seed);
}
