package net.cubecraft.world.chunk.storage.registry;

import java.util.Arrays;

public final class SimpleRegistryStorage<V> implements RegistryStorage<V> {
    private final short[] data;

    public SimpleRegistryStorage() {
        this.data = new short[4096];
    }

    public SimpleRegistryStorage(short[] data) {
        this.data = data;
    }

    public SimpleRegistryStorage(CompressedRegistryStorage<V> storage) {
        this();
        Arrays.fill(this.data, storage._get(0, 0, 0));
    }

    @Override
    public void _set(int x, int y, int z, short id) {
        this.data[x * 256 + y * 16 + z] = id;
    }

    @Override
    public short _get(int x, int y, int z) {
        return this.data[x * 256 + y * 16 + z];
    }

    @Override
    public short[] getData() {
        return this.data;
    }
}
