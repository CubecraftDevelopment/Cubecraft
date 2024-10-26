package net.cubecraft.world.chunk.storage.registry;

public final class CompressedRegistryStorage<V> implements RegistryStorage<V> {
    private final short data;

    public CompressedRegistryStorage(short data) {
        this.data = data;
    }

    public CompressedRegistryStorage(SimpleRegistryStorage<V> s) {
        this.data = s._get(0, 0, 0);
    }

    @Override
    public void _set(int x, int y, int z, short id) {
        throw new UnsupportedOperationException("compressed");
    }

    @Override
    public short _get(int x, int y, int z) {
        return this.data;
    }

    @Override
    public short[] getData() {
        return new short[]{this.data};
    }
}
