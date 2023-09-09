package io.flybird.cubecraft.world.chunk.storage;

public interface ByteStorage {
    byte get(int x, int y, int z);

    void set(int x, int y, int z, byte i);
}
