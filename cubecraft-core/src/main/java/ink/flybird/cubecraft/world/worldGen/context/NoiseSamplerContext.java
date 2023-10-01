package ink.flybird.cubecraft.world.worldGen.context;

import ink.flybird.cubecraft.world.chunk.ProviderChunk;
import ink.flybird.cubecraft.world.worldGen.cache.HeightMap;
import ink.flybird.cubecraft.world.worldGen.noiseGenerator.Noise;

import java.util.HashMap;

public final class NoiseSamplerContext extends ChunkGenerateContext {
    private final HashMap<String, Noise> noiseStorage;
    private final HashMap<String, HeightMap> heightMapStorage;

    public NoiseSamplerContext(
            ProviderChunk chunk,
            long seed,
            HashMap<String, Noise> noiseStorage,
            HashMap<String, HeightMap> heightMapStorage
    ) {
        super(chunk, seed);
        this.noiseStorage = noiseStorage;
        this.heightMapStorage = heightMapStorage;
    }

    public Noise getNoise(String id) {
        return this.noiseStorage.get(id);
    }

    public HeightMap getHeightMap(String id) {
        return this.heightMapStorage.get(id);
    }

    public HeightMap createHeightMap(String id) {
        return this.heightMapStorage.put(id, new HeightMap());
    }
}
