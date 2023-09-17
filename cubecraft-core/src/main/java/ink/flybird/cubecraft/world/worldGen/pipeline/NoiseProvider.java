package ink.flybird.cubecraft.world.worldGen.pipeline;

import ink.flybird.cubecraft.world.worldGen.noiseGenerator.Synth;

import java.util.Map;


public interface NoiseProvider {
    void getNoise(long seed, Map<String, Synth> noises);
}
