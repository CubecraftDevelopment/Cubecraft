package net.cubecraft.event.chunk;

import net.cubecraft.world.World;
import net.cubecraft.world.chunk.Chunk;

public final class ChunkLoadedEvent extends ChunkEvent {
    public ChunkLoadedEvent(Chunk chunk, World world, long x, long z) {
        super(chunk, world, x, z);
    }
}
