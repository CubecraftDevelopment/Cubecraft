package net.cubecraft.world.worldGen.pipeline.pipelines;

import net.cubecraft.world.worldGen.pipeline.WorldGenPipelineBuilder;
import me.gb2022.commons.registry.FieldRegistry;
import me.gb2022.commons.registry.FieldRegistryHolder;

@FieldRegistryHolder("cubecraft")
public interface WorldGeneratorPipelineRegistry {
    @FieldRegistry("overworld")
    WorldGenPipelineBuilder OVERWORLD = new OverworldPipeline();

    @FieldRegistry("flat")
    WorldGenPipelineBuilder FLAT = new OverworldPipeline();
}
