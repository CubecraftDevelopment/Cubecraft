package net.cubecraft.world.chunk;

import net.cubecraft.world.IWorld;
import net.cubecraft.world.chunk.task.ChunkLoadTicket;
import net.cubecraft.world.chunk.pos.ChunkPos;

public interface ChunkLoader {
    void load(IWorld world, ChunkPos pos, ChunkLoadTicket ticket);
}
