package net.cubecraft.world.chunk.pos;

import me.gb2022.commons.container.keymap.Key;
import net.cubecraft.world.chunk.Chunk;
import net.cubecraft.world.entity.Entity;
import org.joml.Vector2d;

@SuppressWarnings("ClassCanBeRecord")
public final class ChunkPos implements Key {
    public static final int DATA_ARRAY_SIZE_2D = Chunk.WIDTH * Chunk.WIDTH;
    public static final int DATA_ARRAY_SIZE_3D = Chunk.WIDTH * Chunk.WIDTH * Chunk.HEIGHT;
    public static final int DATA_FRAGMENT_ARRAY_SIZE = Chunk.WIDTH * Chunk.WIDTH * Chunk.WIDTH;
    public static final long MAX_BLOCK_STORAGE_RADIUS = Integer.MAX_VALUE * 16L;
    private final int x;
    private final int z;


    public ChunkPos(long x, long z) {
        this.x = (int) x;
        this.z = (int) z;
    }

    public static void checkChunkRelativePosition(int x, int y, int z) {
        if (x >= 0 && y >= 0 && z >= 0 && x < Chunk.WIDTH && y < Chunk.HEIGHT && z < Chunk.WIDTH) {
            return;
        }
        if (x < 0 || x >= Chunk.WIDTH) {
            throw new IllegalArgumentException("position out of range:X");
        }
        if (y < 0 || y >= Chunk.HEIGHT) {
            throw new IllegalArgumentException("position out of range:Y");
        }
        throw new IllegalArgumentException("position out of range:Z");
    }

    public static ChunkPos fromWorldPos(double x, double z) {
        return ChunkPos.create(((long) x) >> 4, ((long) z) >> 4);
    }


    public static int world(double wx) {
        return (int) (((long) Math.floor(wx)) >> 4);
    }

    public static int x(Entity e) {
        return world(e.x);
    }

    public static int z(Entity e) {
        return world(e.z);
    }



    public static ChunkPos create(long x, long z) {
        return new ChunkPos(x, z);
    }

    public static int chunkLocal(long w) {
        return (int) (w & 15);
    }

    public static int ofWorld(long v) {
        return (int) (v >> 4);
    }

    public static long toWorld(int c, int off) {
        return c * 16L + off;
    }

    public static long encodeWorldPos(long x, long z) {
        return encode(ofWorld(x), ofWorld(z));
    }

    public static long encode(int x, int z) {
        return (long) x & 4294967295L | ((long) z & 4294967295L) << 32;
    }

    public static int hash(int i, int j) {
        int k = 1664525 * i + 1013904223;
        int l = 1664525 * (j ^ 0xDEADBEEF) + 1013904223;
        return k ^ l;
    }

    public static int getX(long l) {
        return (int) (l & 0xFFFFFFFFL);
    }

    public static int getZ(long l) {
        return (int) (l >>> 32 & 0xFFFFFFFFL);
    }

    public static boolean isWorldPosInvalid(long x, long y, long z) {
        var m = MAX_BLOCK_STORAGE_RADIUS;
        return x > m || x < -m || z > m || z < -m || y < 0 || y >= Chunk.HEIGHT;
    }

    public static ChunkPos fromEntity(Entity e) {
        return fromWorldPos(e.x, e.z);
    }

    public int getX() {
        return this.x;
    }

    public int getZ() {
        return this.z;
    }

    @Override
    public int hashCode() {
        return (int) encode(this.x, this.z);
    }

    public long toWorldPosX(int offset) {
        return (long) x * Chunk.WIDTH + offset;
    }

    public long toWorldPosZ(int offset) {
        return (long) z * Chunk.WIDTH + offset;
    }

    public double distanceToEntity(Entity e) {
        return Math.max(Math.abs(x - e.x / 16), Math.abs(z - e.z / 16));
    }

    public int getRelativePosX(long x) {
        return (int) x & 15;
    }

    public int getRelativePosZ(long z) {
        return (int) z & 15;
    }

    public ChunkPos[] getAllNear() {
        return new ChunkPos[]{ChunkPos.create(this.x - 1, this.z), ChunkPos.create(this.x + 1, this.z), ChunkPos.create(
                this.x,
                this.z - 1
        ), ChunkPos.create(
                this.x,
                this.z + 1
        ),};
    }

    public ChunkPos[] getNearSquared() {
        return new ChunkPos[]{ChunkPos.create(this.x - 1, this.z - 1), ChunkPos.create(this.x - 1, this.z), ChunkPos.create(
                this.x - 1,
                this.z + 1
        ),

                ChunkPos.create(this.x + 1, this.z - 1), ChunkPos.create(this.x + 1, this.z), ChunkPos.create(this.x + 1, this.z + 1),

                ChunkPos.create(this.x, this.z - 1), ChunkPos.create(this.x, this.z + 1),};
    }

    public long pack() {
        return encode(this.x, this.z);
    }

    public Vector2d toWorldPos(int ox, int oz) {
        return new Vector2d(toWorldPosX(ox), toWorldPosZ(oz));
    }
}
