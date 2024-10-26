package net.cubecraft.world.worldGen.structure;

import net.cubecraft.world.chunk.Chunk;
import net.cubecraft.world.chunk.ChunkCache;
import net.cubecraft.world.chunk.WorldChunk;

public interface StructureController {

    default void generateChunk(Chunk chunk, ChunkCache<WorldChunk> cache) {
        var x0 = chunk.getKey().toWorldPosX(-this.radius());
        var x1 = chunk.getKey().toWorldPosX(this.radius() + 16);
        var z0 = chunk.getKey().toWorldPosZ(-this.radius());
        var z1 = chunk.getKey().toWorldPosZ(this.radius() + 16);

        var y0 = this.bottomBound();
        var y1 = Chunk.HEIGHT - this.topBound();

        for (long x = x0; x <= x1; x++) {
            for (long z = z0; z <= z1; z++) {
                this.attempt(chunk, x, z, y0, y1, cache);
            }
        }
    }

    void attempt(Chunk chunk, long x, long z, int y0, int y1, ChunkCache<WorldChunk> cache);

    StructureShape getShape();

    default int radius() {
        return getShape().radius();
    }

    default int topBound() {
        return getShape().topBound();
    }

    default int bottomBound() {
        return getShape().bottomBound();
    }
}
