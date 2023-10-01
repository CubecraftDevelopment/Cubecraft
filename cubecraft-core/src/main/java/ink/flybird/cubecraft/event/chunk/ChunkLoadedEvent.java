package ink.flybird.cubecraft.event.chunk;

import ink.flybird.cubecraft.world.IWorld;
import ink.flybird.cubecraft.world.chunk.Chunk;

public final class ChunkLoadedEvent extends ChunkEvent {
    public ChunkLoadedEvent(Chunk chunk, IWorld world, long x, long z) {
        super(chunk, world, x, z);
    }
}
