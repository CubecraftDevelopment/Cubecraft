package net.cubecraft.world.chunk;

import net.cubecraft.world.World;
import net.cubecraft.world.chunk.pos.ChunkPos;
import net.cubecraft.world.chunk.task.ChunkLoadTicket;

public interface ChunkLoader {
    void load(World world, ChunkPos pos, ChunkLoadTicket ticket);
}
