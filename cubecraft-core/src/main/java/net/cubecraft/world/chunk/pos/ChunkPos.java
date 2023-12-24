package net.cubecraft.world.chunk.pos;

import ink.flybird.fcommon.container.Key;
import net.cubecraft.world.chunk.Chunk;
import net.cubecraft.world.entity.Entity;

import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("ClassCanBeRecord")
public final class ChunkPos implements Key {
    public static final int DATA_ARRAY_SIZE_2D = Chunk.WIDTH * Chunk.WIDTH;
    public static final int DATA_ARRAY_SIZE_3D = Chunk.WIDTH * Chunk.WIDTH * Chunk.HEIGHT;
    public static final int DATA_FRAGMENT_ARRAY_SIZE = Chunk.WIDTH * Chunk.WIDTH * Chunk.WIDTH;
    private final long x;
    private final long z;

    public ChunkPos(long x, long z) {
        this.x = x;
        this.z = z;
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

    public static ChunkPos fromWorldPos(long x, long z) {
        return ChunkPos.create(x >> 4, z >> 4);
    }

    public static void checkChunkSectionRelativePosition(int x, int y, int z) {
        if (x >= 0 && y >= 0 && z >= 0 && x < Chunk.WIDTH && y < Chunk.WIDTH && z < Chunk.WIDTH) {
            return;
        }
        if (x < 0 || x >= Chunk.WIDTH) {
            throw new IllegalArgumentException("position out of range:X");
        }
        if (y < 0 || y >= Chunk.WIDTH) {
            throw new IllegalArgumentException("position out of range:Y");
        }
        throw new IllegalArgumentException("position out of range:Z");
    }

    //todo:常量池回收问题
    public static ChunkPos create(long x, long z) {
        return new ChunkPos(x, z);
    }


    public static final int HASH_KEY_0=1664525;
    public static final long HASH_KEY_1=101390422321321413L;

    public static int encode(long x, long z) {
        int i = (int) (HASH_KEY_0 * (int)(x + HASH_KEY_1)&-HASH_KEY_1);
        int j = HASH_KEY_0 * (int)((int)(z ^ -559038737*Integer.MAX_VALUE) + 1013904223*Integer.MAX_VALUE);
        return i ^ j;
    }

    public long getX() {
        return this.x;
    }

    public long getZ() {
        return this.z;
    }

    @Override
    public int hashCode() {
        return encode(this.x, this.z);
    }

    public long toWorldPosX(int offset) {
        return x * Chunk.WIDTH + offset;
    }

    public long toWorldPosZ(int offset) {
        return z * Chunk.WIDTH + offset;
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
        return new ChunkPos[]{
                ChunkPos.create(this.x - 1, this.z),
                ChunkPos.create(this.x - 1, this.z),
                ChunkPos.create(this.x, this.z - 1),
                ChunkPos.create(this.x, this.z + 1),
        };
    }
}
