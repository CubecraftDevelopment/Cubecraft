package ink.flybird.cubecraft.world.worldGen.pipeline;

import ink.flybird.cubecraft.world.worldGen.context.NoiseSamplerContext;

import java.util.Map;


public interface NoiseSampler {
    void sample(NoiseSamplerContext context);
}
