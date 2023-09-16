package ink.flybird.cubecraft.world.chunk.storage;

public interface StringStorage {
    String get(int x, int y, int z);

    void set(int x, int y, int z, String i);
}
