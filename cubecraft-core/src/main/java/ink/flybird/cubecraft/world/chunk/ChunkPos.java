package ink.flybird.cubecraft.world.chunk;

import ink.flybird.fcommon.container.Key;
import ink.flybird.fcommon.math.MathHelper;
import ink.flybird.cubecraft.world.entity.Entity;

public record ChunkPos(long x, long z) implements Key {
    public static final int DATA_ARRAY_SIZE_2D = Chunk.WIDTH * Chunk.WIDTH;
    public static final int DATA_ARRAY_SIZE_3D = Chunk.WIDTH * Chunk.WIDTH * Chunk.HEIGHT;
    public static final int DATA_FRAGMENT_ARRAY_SIZE = Chunk.WIDTH * Chunk.WIDTH * Chunk.WIDTH;

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
        return new ChunkPos(
                MathHelper.getChunkPos(x, Chunk.WIDTH),
                MathHelper.getChunkPos(z, Chunk.WIDTH)
        );
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


    @Override
    public String toString() {
        return x + "/" + z;
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
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
        return (int) MathHelper.getRelativePosInChunk(x, Chunk.WIDTH);
    }

    public int getRelativePosZ(long z) {
        return (int) MathHelper.getRelativePosInChunk(z, Chunk.WIDTH);
    }

    public ChunkPos[] getAllNear() {
        return new ChunkPos[]{
                new ChunkPos(this.x - 1, this.z),
                new ChunkPos(this.x - 1, this.z),
                new ChunkPos(this.x, this.z - 1),
                new ChunkPos(this.x, this.z + 1),
        };
    }
}
