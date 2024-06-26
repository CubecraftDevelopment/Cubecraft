package net.cubecraft.world.dimension;

import net.cubecraft.world.IWorld;
import net.cubecraft.world.block.EnumFacing;
import net.cubecraft.world.chunk.Chunk;

public interface Dimension {
    long VALID_WORLD_RADIUS = Long.MAX_VALUE;

    String predictBlockID(IWorld world, long x, long y, long z);

    default Byte predictLightAt(IWorld world, long x, long y, long z) {
        if (outsideWorld(x, z)) {
            return 0;
        }
        if (outsideWorldVertical(y)) {
            return 127;
        }
        return null;
    }

    default EnumFacing predictBlockFacingAt(IWorld world, long x, long y, long z) {
        if (outsideWorld(x, z)) {
            return EnumFacing.Up;
        }
        if (outsideWorldVertical(y)) {
            return EnumFacing.Up;
        }
        return null;
    }

    default Byte predictBlockMetaAt(IWorld world, long x, long y, long z) {
        if (outsideWorld(x, z)) {
            return 0;
        }
        if (outsideWorldVertical(y)) {
            return 0;
        }
        return null;
    }

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
}