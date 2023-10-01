package ink.flybird.cubecraft.world.worldGen.context;

import ink.flybird.cubecraft.world.chunk.ProviderChunk;
import ink.flybird.cubecraft.world.worldGen.noiseGenerator.Noise;

import java.util.HashMap;

public final class NoiseProviderContext extends ChunkGenerateContext {
    private final HashMap<String, Noise> noiseStorage;

    public NoiseProviderContext(
            ProviderChunk chunk,
            long seed,
            HashMap<String, Noise> noiseStorage
    ) {
        super(chunk, seed);
        this.noiseStorage = noiseStorage;
    }

    public Noise getNoise(String id) {
        return this.noiseStorage.get(id);
    }

    public Noise createNoise(String id, Noise n) {
        return this.noiseStorage.put(id, n);
    }
}
