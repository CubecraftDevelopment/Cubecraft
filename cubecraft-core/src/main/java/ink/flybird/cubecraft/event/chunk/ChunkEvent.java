package ink.flybird.cubecraft.event.chunk;

import ink.flybird.cubecraft.world.IWorld;
import ink.flybird.cubecraft.world.chunk.Chunk;

public abstract class ChunkEvent {
    private final Chunk chunk;
    private final IWorld world;
    private final long x;
    private final long z;

    protected ChunkEvent(Chunk chunk, IWorld world, long x, long z) {
        this.chunk = chunk;
        this.world = world;
        this.x = x;
        this.z = z;
    }

    public Chunk getChunk() {
        return this.chunk;
    }

    public IWorld getWorld() {
        return this.world;
    }

    public long getX() {
        return this.x;
    }

    public long getZ() {
        return this.z;
    }
}
