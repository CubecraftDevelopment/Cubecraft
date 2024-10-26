package net.cubecraft.world.dimension;

import net.cubecraft.world.World;
import net.cubecraft.world.block.EnumFacing;
import net.cubecraft.world.chunk.Chunk;

public interface Dimension {
    long VALID_WORLD_RADIUS = Long.MAX_VALUE - 1024;

    static boolean outsideWorld(long x, long z) {
        if (Math.abs(x) >= VALID_WORLD_RADIUS) {
            return true;
        }
        return Math.abs(z) >= VALID_WORLD_RADIUS;
    }

    static boolean outsideWorldVertical(long y) {
        if (y >= Chunk.HEIGHT) {
            return true;
        }
        return y < 0;
    }

    String predictBlockID(World world, long x, long y, long z);

    default Byte predictLightAt(World world, long x, long y, long z) {
        if (outsideWorld(x, z)) {
            return 0;
        }
        if (outsideWorldVertical(y)) {
            return 127;
        }
        return null;
    }

    default EnumFacing predictBlockFacingAt(World world, long x, long y, long z) {
        if (outsideWorld(x, z)) {
            return EnumFacing.Up;
        }
        if (outsideWorldVertical(y)) {
            return EnumFacing.Up;
        }
        return null;
    }

    default byte getBlockMeta(World world, long x, long y, long z) {
        return 0;
    }


    int getBlockID(World world, long x, long y, long z);

    byte getBlockLight(World world, long x, long y, long z);
}