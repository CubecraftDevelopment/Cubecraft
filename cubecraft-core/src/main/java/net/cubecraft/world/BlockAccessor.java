package net.cubecraft.world;

import net.cubecraft.world.block.access.IBlockAccess;

public interface BlockAccessor {
    IBlockAccess getBlockAccess(long x, long y, long z);
}
