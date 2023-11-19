package net.cubecraft.event.chunk;

import net.cubecraft.world.IWorld;
import net.cubecraft.world.chunk.Chunk;

public final class ChunkUnloadEvent extends ChunkEvent {
    public ChunkUnloadEvent(Chunk chunk, IWorld world, long x, long z) {
        super(chunk, world, x, z);
    }
}
