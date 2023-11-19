package net.cubecraft.world.worldGen.generator;

import net.cubecraft.world.worldGen.pipeline.WorldGenPipelineBuilder;
import ink.flybird.fcommon.registry.FieldRegistry;
import ink.flybird.fcommon.registry.FieldRegistryHolder;

@FieldRegistryHolder("cubecraft")
public interface WorldGeneratorPipelineRegistry {
    @FieldRegistry("overworld")
    WorldGenPipelineBuilder OVERWORLD = new OverworldPipeline();

    @FieldRegistry("flat")
    WorldGenPipelineBuilder FLAT = new OverworldPipeline();
}
