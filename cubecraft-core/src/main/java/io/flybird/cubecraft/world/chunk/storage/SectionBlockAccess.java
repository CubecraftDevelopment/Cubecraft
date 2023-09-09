package io.flybird.cubecraft.world.chunk.storage;

import io.flybird.cubecraft.world.block.EnumFacing;

public interface SectionBlockAccess {
    String getBlockID(int x, int y, int z);

    EnumFacing getBlockFacing(int x, int y, int z);

    byte getBlockMeta(int x, int y, int z);

    void setBlockID(int x, int y, int z, String id);

    void setBlockFacing(int x, int y, int z, EnumFacing facing);

    void setBlockMeta(int x, int y, int z, byte meta);
}
