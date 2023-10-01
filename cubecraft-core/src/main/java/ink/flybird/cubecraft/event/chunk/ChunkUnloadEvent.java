package ink.flybird.cubecraft.event.chunk;

import ink.flybird.cubecraft.world.IWorld;
import ink.flybird.cubecraft.world.chunk.Chunk;

public final class ChunkUnloadEvent extends ChunkEvent {
    public ChunkUnloadEvent(Chunk chunk, IWorld world, long x, long z) {
        super(chunk, world, x, z);
    }
}
