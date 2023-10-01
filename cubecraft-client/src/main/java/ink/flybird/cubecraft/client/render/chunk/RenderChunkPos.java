package ink.flybird.cubecraft.client.render.chunk;

import ink.flybird.cubecraft.client.internal.registry.ClientSettingRegistry;
import ink.flybird.cubecraft.client.render.DistanceComparable;
import ink.flybird.cubecraft.world.entity.Entity;
import ink.flybird.fcommon.container.Key;
import ink.flybird.fcommon.math.AABB;
import ink.flybird.quantum3d_legacy.Camera;
import org.joml.Vector3d;

import java.util.concurrent.ConcurrentHashMap;

public final class RenderChunkPos implements Key, DistanceComparable {
    private static final ConcurrentHashMap<String, RenderChunkPos> CONSTANT_POOL = new ConcurrentHashMap<>(1024);
    private final long x, y, z;

    public RenderChunkPos(long x, long y, long z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public RenderChunkPos(String s) {
        this.x = Long.parseLong(s.split("/")[0]);
        this.y = Long.parseLong(s.split("/")[1]);
        this.z = Long.parseLong(s.split("/")[2]);
    }

    public static AABB getAABBFromPos(RenderChunkPos renderChunkPos, Camera camera) {
        return new AABB((renderChunkPos.getX() * 16 - camera.getPosition().x), (renderChunkPos.getY() * 16 - camera.getPosition().y), (renderChunkPos.getZ() * 16 - camera.getPosition().z), (renderChunkPos.getX() * 16 + 16 - camera.getPosition().x), (renderChunkPos.getY() * 16 + 16 - camera.getPosition().y), (renderChunkPos.getZ() * 16 + 16 - camera.getPosition().z));
    }

    //todo:常量池回收问题
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

    @Override
    public double distanceTo(Entity target) {
        return this.getWorldPosition().add(8, 8, 8).distance(target.getPosition());
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
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
