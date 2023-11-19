package net.cubecraft.world.chunk.storage;

public class CompressedStringStorage implements StringStorage {
    private final String data;

    public CompressedStringStorage(String data) {
        this.data = data;
    }

    public CompressedStringStorage(StringStorage storage) {
        this.data=storage.get(0,0,0);
    }

    @Override
    public String get(int x, int y, int z) {
        return this.data;
    }

    @Override
    public void set(int x, int y, int z, String i) {
        throw new UnsupportedOperationException("compressed");
    }
}
