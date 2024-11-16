package net.cubecraft.world.access;

import net.cubecraft.world.World;
import net.cubecraft.world.chunk.pos.ChunkPos;
import net.cubecraft.world.chunk.task.ChunkLoadTicket;

public interface ChunkLoadAccess {
    //load
    static void loadChunkRange(World world, ChunkPos pos, int range, ChunkLoadTicket ticket) {
        int centerCX = pos.getX();
        int centerCZ = pos.getZ();
        for (int x = centerCX - range; x <= centerCX + range; x++) {
            for (int z = centerCZ - range; z <= centerCZ + range; z++) {
                world.loadChunk(x, z, ticket);
            }
        }
    }
}
