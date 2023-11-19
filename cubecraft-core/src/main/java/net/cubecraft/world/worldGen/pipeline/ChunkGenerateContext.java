package net.cubecraft.world.worldGen.pipeline;

import net.cubecraft.world.chunk.ProviderChunk;
import net.cubecraft.world.worldGen.cache.HeightMap;
import net.cubecraft.world.worldGen.noiseGenerator.Noise;

import java.util.HashMap;

public final class ChunkGenerateContext {
    private final HashMap<String, Noise> noiseStorage = new HashMap<>();
    private final HashMap<String, HeightMap> heightMapStorage = new HashMap<>();
    private final ProviderChunk chunk;
    private final ChunkGeneratorPipeline pipeline;

    public ChunkGenerateContext(ProviderChunk chunk, ChunkGeneratorPipeline pipeline) {
        this.chunk = chunk;
        this.pipeline = pipeline;
    }

    public <T extends Noise> T getNoise(String id, Class<T> type) {
        return type.cast(this.noiseStorage.get(id));
    }

    public Noise createNoise(String id, Noise n) {
        this.noiseStorage.put(id, n);
        return n;
    }

    public HeightMap getHeightMap(String id) {
        return this.heightMapStorage.get(id);
    }

    public HeightMap createHeightMap(String id) {
        HeightMap m = new HeightMap();
        this.heightMapStorage.put(id, m);
        return m;
    }

    public ProviderChunk getChunk() {
        return chunk;
    }

    public long getSeed() {
        return this.getPipeline().getSeed();
    }

    public ChunkGeneratorPipeline getPipeline() {
        return pipeline;
    }
}
