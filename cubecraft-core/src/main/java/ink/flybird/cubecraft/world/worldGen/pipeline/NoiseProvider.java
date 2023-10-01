package ink.flybird.cubecraft.world.worldGen.pipeline;

import ink.flybird.cubecraft.world.worldGen.context.NoiseProviderContext;
import ink.flybird.cubecraft.world.worldGen.noiseGenerator.Noise;

import java.util.Map;

//todo: pipeline and calling
public interface NoiseProvider {
    void generateNoise(NoiseProviderContext context);
}
