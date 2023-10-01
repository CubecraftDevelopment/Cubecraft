package ink.flybird.cubecraft.world.worldGen.pipeline;

//todo:config
public interface PipelineBuilder {
    ChunkGeneratorPipeline build(String worldType, String seed);
}
