package net.cubecraft.world.chunk;

import net.cubecraft.world.chunk.pos.ChunkPos;
import net.cubecraft.world.worldGen.pipeline.ChunkContext;

public final class PrimerChunk extends Chunk {
    private final ChunkContext context = new ChunkContext();

    public PrimerChunk(ChunkPos p) {
        super(p.getX(), p.getZ());
    }

    public ChunkContext getContext() {
        return context;
    }
}
