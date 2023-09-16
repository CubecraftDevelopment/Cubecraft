package ink.flybird.cubecraft.world.chunk.storage;

public interface SectionLightAccess {
    byte getBlockLight(int x, int y, int z);

    void setBlockLight(int x, int y, int z, byte light);
}