package ink.flybird.cubecraft.world.chunk.storage;

public class CompressedByteStorage implements ByteStorage {
    private final byte data;

    public CompressedByteStorage(byte data) {
        this.data = data;
    }

    public CompressedByteStorage(ByteStorage storage) {
        this.data=storage.get(0,0,0);
    }

    @Override
    public byte get(int x, int y, int z) {
        return this.data;
    }

    @Override
    public void set(int x, int y, int z, byte i) {
        throw new UnsupportedOperationException("compressed");
    }
}
