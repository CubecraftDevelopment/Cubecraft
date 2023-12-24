package net.cubecraft.client.render.chunk;

import ink.flybird.fcommon.container.Key;
import ink.flybird.fcommon.math.AABB;
import net.cubecraft.client.ClientSettingRegistry;
import net.cubecraft.client.render.DistanceComparable;
import net.cubecraft.world.entity.Entity;
import org.joml.Vector3d;

import java.util.concurrent.ConcurrentHashMap;

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

    public static AABB getAABBFromPos(RenderChunkPos renderChunkPos, Vector3d viewOffset) {
        double x = renderChunkPos.getWorldX() - viewOffset.x();
        double y = renderChunkPos.getWorldY() - viewOffset.y();
        double z = renderChunkPos.getWorldZ() - viewOffset.z();

        return new AABB(x, y, z, x + 16, y + 16, z + 16);
    }

    public static RenderChunkPos create(long x, long y, long z) {
        if (ClientSettingRegistry.DISABLE_CONSTANT_POOL.getValue()) {
            return new RenderChunkPos(x, y, z);
        }
        String k = encode(x, y, z);
        if (!CONSTANT_POOL.containsKey(k)) {
            CONSTANT_POOL.put(k, new RenderChunkPos(x, y, z));
        }
        return CONSTANT_POOL.get(k);
    }

    public static String encode(long x, long y, long z) {
        return x + "/" + y + "/" + z;
    }

    public AABB getAABB(Vector3d viewOffset) {
        return getAABBFromPos(this, viewOffset);
    }

    @Override
    public double distanceTo(Entity target) {
        return this.getWorldPosition().add(8, 8, 8).distance(target.getPosition());
    }

    public double distanceTo(Vector3d pos) {
        return this.getWorldPosition().add(8, 8, 8).distance(pos);
    }

    @Override
    public int hashCode() {
        long hashValue = 0;

        hashValue = (hashValue * HASH_KEY_0) + this.getX() ^ -HASH_KEY_2;
        hashValue = (hashValue * HASH_KEY_1) + this.getY() | HASH_KEY_0;
        hashValue = ((hashValue ^ HASH_KEY_1) * HASH_KEY_0) + this.getZ();

        return (int) (hashValue % HASH_KEY_2);
    }

    @Override
    public String toString() {
        return encode(this.getX(), this.getY(), this.getZ());
    }

    public Vector3d getWorldPosition() {
        return new Vector3d(this.getWorldX(), this.getWorldY(), this.getWorldZ());
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
}
