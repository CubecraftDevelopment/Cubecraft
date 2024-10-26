package net.cubecraft.world.worldGen.pipeline;


import net.cubecraft.world.worldGen.noise.Noise;

import java.util.HashMap;

public final class PipelineContext {
    private ChunkGeneratorPipeline pipeline;
    private final HashMap<String, Noise> noiseStorage = new HashMap<>();
    private final long seed;

    public PipelineContext(long seed) {
        this.seed = seed;
    }

    public <T extends Noise> T getNoise(String id, Class<T> type) {
        return type.cast(this.noiseStorage.get(id));
    }

    public Noise createNoise(String id, Noise n) {
        if(this.noiseStorage.containsKey(id)) {
            return this.noiseStorage.get(id);
        }
        this.noiseStorage.put(id, n);
        return n;
    }

    public long getSeed() {
        return this.seed;
    }

    public ChunkGeneratorPipeline getPipeline() {
        return pipeline;
    }
}
