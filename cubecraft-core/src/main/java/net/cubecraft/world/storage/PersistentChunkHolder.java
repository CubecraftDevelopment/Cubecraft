package net.cubecraft.world.storage;

import net.cubecraft.world.World;
import net.cubecraft.world.chunk.WorldChunk;

import java.util.Set;

public interface PersistentChunkHolder {
    void save(WorldChunk chunk);

    void save(Set<WorldChunk> chunks);

    WorldChunk load(World world, int x, int z);
}
