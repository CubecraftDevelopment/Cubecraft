package net.cubecraft.client.render.chunk;

import ink.flybird.fcommon.container.Key;
import ink.flybird.fcommon.math.AABB;
import net.cubecraft.client.ClientSettingRegistry;
import net.cubecraft.client.render.DistanceComparable;
import net.cubecraft.world.entity.Entity;
import org.joml.Vector3d;

import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("ClassCanBeRecord")
public final class RenderChunkPos implements Key, DistanceComparable {
    public static final long HASH_KEY_0 = 63615134589L;
    public static final long HASH_KEY_1 = 37855153351311L;
    public static final long HASH_KEY_2 = 2147483647;

    private static final ConcurrentHashMap<String, RenderChunkPos> CONSTANT_POOL = new ConcurrentHashMap<>(1024);
    private final long x, y, z;

    public RenderChunkPos(long x, long y, long z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }


    public static AABB getBounding(RenderChunkPos renderChunkPos, Vector3d viewOffset) {
        double x = renderChunkPos.getWorldX() - viewOffset.x();
        double y = renderChunkPos.getWorldY() - viewOffset.y();
        double z = renderChunkPos.getWorldZ() - viewOffset.z();

        return new AABB(x, y, z, x + 16, y + 16, z + 16);
    }

    public static RenderChunkPos create(long x, long y, long z) {
        if (ClientSettingRegistry.DISABLE_CONSTANT_POOL.getValue()) {
            return new RenderChunkPos(x, y, z);
        }
        String k = toString(x, y, z);
        if (!CONSTANT_POOL.containsKey(k)) {
            CONSTANT_POOL.put(k, new RenderChunkPos(x, y, z));
        }
        return CONSTANT_POOL.get(k);
    }

    public static Vector3d toWorldPos(long x, long y, long z) {
        return new Vector3d(x * 16 + 8, y * 16 + 8, z * 16 + 8);
    }

    public static String toString(long x, long y, long z) {
        return "render_pos{x=%d,y=%d,z=%d}".formatted(x, y, z);
    }

    public static int hash(RenderChunkPos pos) {
        long hashValue = -3;

        hashValue = (hashValue * HASH_KEY_0) + pos.getX() ^ -HASH_KEY_2;
        hashValue = (hashValue * HASH_KEY_1) + pos.getY() | HASH_KEY_0;
        hashValue = ((hashValue ^ HASH_KEY_1) * HASH_KEY_0) + pos.getZ();

        return (int) (hashValue % HASH_KEY_2);
    }

    public static double distanceTo(long cx, long cy, long cz, Vector3d vec) {
        return vec.distance(cx * 16 + 8, cy * 16 + 8, cz * 16 + 8);
    }

    public static double chunkDistanceTo(long cx, long cy, long cz, Vector3d vec) {
        long fx = (((long) vec.x) >> 4) * 16 + 8;
        long fy = (((long) vec.y) >> 4) * 16 + 8;
        long fz = (((long) vec.z) >> 4) * 16 + 8;
        return new Vector3d(fx, fy, fz).distance(cx * 16 + 8, cy * 16 + 8, cz * 16 + 8);
    }

    public static Vector3d getCenterWorldPosition(long cx, long cy, long cz) {
        return new Vector3d(cx * 16 + 8, cy * 16 + 8, cz * 16 + 8);
    }

    public static Vector3d getBaseWorldPosition(long cx, long cy, long cz) {
        return new Vector3d(cx * 16, cy * 16, cz * 16);
    }


    @Override
    public double distanceTo(Entity target) {
        return distanceTo(this.x, this.y, this.z, target.getPosition());
    }

    public double distanceTo(Vector3d pos) {
        return distanceTo(this.x, this.y, this.z, pos);
    }

    public double chunkDistanceTo(Vector3d vec) {
        return chunkDistanceTo(this.x, this.y, this.z, vec);
    }

    public Vector3d getBaseWorldPosition() {
        return getBaseWorldPosition(this.x, this.y, this.z);
    }

    public Vector3d getCenterWorldPosition() {
        return getCenterWorldPosition(this.x, this.y, this.z);
    }

    public long getX() {
        return x;
    }

    public long getY() {
        return y;
    }

    public long getZ() {
        return z;
    }

    public long getWorldX() {
        return getX() * 16;
    }

    public long getWorldY() {
        return getY() * 16;
    }

    public long getWorldZ() {
        return getZ() * 16;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof RenderChunkPos p)) {
            return false;
        }
        return p.x == this.x && p.y == this.y && p.z == this.z;
    }

    @Override
    public int hashCode() {
        return hash(this);
    }

    @Override
    public String toString() {
        return toString(this.getX(), this.getY(), this.getZ());
    }

    public AABB getBounding(Vector3d viewOffset) {
        return getBounding(this, viewOffset);
    }
}
