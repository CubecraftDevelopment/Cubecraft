package ink.flybird.cubecraft.world.worldGen.context;

import ink.flybird.cubecraft.world.chunk.ProviderChunk;

public abstract class ChunkGenerateContext {
    private final ProviderChunk chunk;
    private final long seed;

    public ChunkGenerateContext(ProviderChunk chunk, long seed) {
        this.chunk = chunk;
        this.seed = seed;
    }

    public ProviderChunk getChunk() {
        return chunk;
    }

    public long getSeed() {
        return seed;
    }
}
