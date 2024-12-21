package net.cubecraft.client.render.chunk;

import me.gb2022.commons.math.AABB;
import org.joml.Vector3d;

@SuppressWarnings("ClassCanBeRecord")
public interface RenderChunkPos {
    long HASH_KEY_0 = 63615134589L;
    long HASH_KEY_1 = 37855153351311L;
    long HASH_KEY_2 = 2147483647;

    static Vector3d toWorldPos(long x, long y, long z) {
        return new Vector3d(x * 16 + 8, y * 16 + 8, z * 16 + 8);
    }

    static String toString(long x, long y, long z) {
        return "render_pos{x=%d,y=%d,z=%d}".formatted(x, y, z);
    }

    static int hash(long x, long y, long z) {
        long hashValue = -3;

        hashValue = (hashValue * HASH_KEY_0) + x ^ -HASH_KEY_2;
        hashValue = (hashValue * HASH_KEY_1) + y | HASH_KEY_0;
        hashValue = ((hashValue ^ HASH_KEY_1) * HASH_KEY_0) + z;

        return (int) (hashValue % HASH_KEY_2);
    }

    static double distanceTo(long cx, long cy, long cz, Vector3d vec) {
        return vec.distance(cx * 16 + 8, cy * 16 + 8, cz * 16 + 8);
    }

    static double chunkDistanceTo(long cx, long cy, long cz, Vector3d vec) {
        long fx = (((long) vec.x) >> 4) * 16 + 8;
        long fy = (((long) vec.y) >> 4) * 16 + 8;
        long fz = (((long) vec.z) >> 4) * 16 + 8;
        return new Vector3d(fx, fy, fz).distance(cx * 16 + 8, cy * 16 + 8, cz * 16 + 8);
    }

    static Vector3d getCenterWorldPosition(long cx, long cy, long cz) {
        return new Vector3d(cx * 16 + 8, cy * 16 + 8, cz * 16 + 8);
    }

    static Vector3d getBaseWorldPosition(long cx, long cy, long cz) {
        return new Vector3d(cx * 16, cy * 16, cz * 16);
    }

    static AABB getBounding(Vector3d viewOffset, int x1, int y1, int z1) {
        double x = (x1 << 4) - viewOffset.x();
        double y = (y1 << 4) - viewOffset.y();
        double z = (z1 << 4) - viewOffset.z();

        return new AABB(x, y, z, x + 16, y + 16, z + 16);
    }

    static AABB getBounding(double vx, double vy, double vz, int x1, int y1, int z1) {
        double x = (x1 << 4) - vx;
        double y = (y1 << 4) - vy;
        double z = (z1 << 4) - vz;

        return new AABB(x, y, z, x + 16, y + 16, z + 16);
    }
}
