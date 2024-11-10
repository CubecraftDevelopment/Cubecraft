package net.cubecraft.world.access;

import net.cubecraft.world.World;
import net.cubecraft.world.chunk.task.ChunkLoadTicket;
import net.cubecraft.world.chunk.pos.ChunkPos;

public interface ChunkLoadAccess {
    //load
    static void loadChunkRange(World world, ChunkPos pos, int range, ChunkLoadTicket ticket) {
        long centerCX = pos.getX();
        long centerCZ = pos.getZ();
        for (long x = centerCX - range; x <= centerCX + range; x++) {
            for (long z = centerCZ - range; z <= centerCZ + range; z++) {
                world.loadChunk(ChunkPos.create(x, z), ticket);
            }
        }
    }
}
