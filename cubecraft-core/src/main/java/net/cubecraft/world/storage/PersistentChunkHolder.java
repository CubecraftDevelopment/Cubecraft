package net.cubecraft.world.storage;

import net.cubecraft.world.World;
import net.cubecraft.world.chunk.WorldChunk;

public interface PersistentChunkHolder {
    void save(WorldChunk chunk);

    WorldChunk load(World world, int x, int z);
}
