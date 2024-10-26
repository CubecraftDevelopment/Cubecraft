package net.cubecraft.event.chunk;

import net.cubecraft.world.World;
import net.cubecraft.world.chunk.Chunk;

public abstract class ChunkEvent {
    private final Chunk chunk;
    private final World world;
    private final long x;
    private final long z;

    protected ChunkEvent(Chunk chunk, World world, long x, long z) {
        this.chunk = chunk;
        this.world = world;
        this.x = x;
        this.z = z;
    }

    public Chunk getChunk() {
        return this.chunk;
    }

    public World getWorld() {
        return this.world;
    }

    public long getX() {
        return this.x;
    }

    public long getZ() {
        return this.z;
    }
}
