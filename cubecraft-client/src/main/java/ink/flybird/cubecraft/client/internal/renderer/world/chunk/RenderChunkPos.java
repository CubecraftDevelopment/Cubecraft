package ink.flybird.cubecraft.client.internal.renderer.world.chunk;

import ink.flybird.quantum3d.Camera;
import ink.flybird.fcommon.container.Key;
import ink.flybird.fcommon.math.AABB;
import ink.flybird.cubecraft.client.render.DistanceComparable;
import io.flybird.cubecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;

public class RenderChunkPos implements Key, DistanceComparable, Comparable<RenderChunkPos> {
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
        return new AABB(
                (renderChunkPos.getX() * 16 - camera.getPosition().x),
                (renderChunkPos.getY() * 16 - camera.getPosition().y),
                (renderChunkPos.getZ() * 16 - camera.getPosition().z),
                (renderChunkPos.getX() * 16 + 16 - camera.getPosition().x),
                (renderChunkPos.getY() * 16 + 16 - camera.getPosition().y),
                (renderChunkPos.getZ() * 16 + 16 - camera.getPosition().z)
        );
    }

    @Override
    public int compareTo(@NotNull RenderChunkPos o) {
        return 0;
    }


    @Override
    public double distanceTo(Entity target) {
        return new Vector3d(this.getWorldX(), this.getWorldY(), this.getWorldZ()).add(8,8,8).distance(target.getPosition());
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public String toString() {
        return this.x +
                "/" +
                this.y +
                "/" +
                this.z +
                "/";
    }

    public Vector3d clipToWorldPosition() {
        return new Vector3d(x * 16, y * 16, z * 16);
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
}
