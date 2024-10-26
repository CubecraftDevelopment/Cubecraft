package net.cubecraft.world.chunk.storage.registry;

public interface RegistryStorage<V> {
    void _set(int x, int y, int z, short id);

    short _get(int x, int y, int z);

    short[] getData();
}
