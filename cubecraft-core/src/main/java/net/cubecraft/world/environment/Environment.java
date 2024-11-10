package net.cubecraft.world.environment;

import me.gb2022.commons.math.AABB;
import net.cubecraft.util.register.Named;
import net.cubecraft.world.World;
import net.cubecraft.world.block.EnumFacing;
import net.cubecraft.world.chunk.Chunk;

public interface Environment extends Named {

    long VALID_WORLD_RADIUS = 2147483647 * 16L - 512;

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

    default AABB[] getBounding(AABB bound) {
        var r = VALID_WORLD_RADIUS;

        var x0 = bound.x0;
        var y0 = bound.y0;
        var z0 = bound.z0;
        var x1 = bound.x1;
        var y1 = bound.y1;
        var z1 = bound.z1;

        return new AABB[]{
                new AABB(r + 1, y0, z0, r + 2, y1, z1),
                new AABB(-r - 1, y0, z0, 1 - r, y1, z1),
                new AABB(x0, y0, r + 1, x1, y1, r + 2),
                new AABB(x0, y0, -r - 1, x1, y1, 1 - r),
        };
    }

    int getBlockID(World world, long x, long y, long z);

    byte getBlockLight(World world, long x, long y, long z);
}