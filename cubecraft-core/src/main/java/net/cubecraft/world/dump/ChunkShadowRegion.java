package net.cubecraft.world.dump;

import net.cubecraft.world.BlockAccessor;
import net.cubecraft.world.World;
import net.cubecraft.world.block.access.ChunkBlockAccess;
import net.cubecraft.world.block.access.IBlockAccess;
import net.cubecraft.world.chunk.WorldChunk;
import net.cubecraft.world.chunk.pos.ChunkPos;

public final class ChunkShadowRegion implements BlockAccessor {
    private final WorldChunk[] cache;
    private final World world;

    private final int cx0;
    private final int cz0;
    private final int cx1;
    private final int cz1;
    private final int xLen;
    private final int zLen;

    public ChunkShadowRegion(World world, int x0, int z0, int x1, int z1) {
        this.cx0 = x0;
        this.world = world;
        this.cz0 = z0;
        this.cx1 = x1;
        this.cz1 = z1;

        this.xLen = this.cx1 - this.cx0 + 1;
        this.zLen = this.cz1 - this.cz0 + 1;

        this.cache = new WorldChunk[this.xLen * this.zLen];

        for (var x = 0; x < this.xLen; x++) {
            for (var z = 0; z < this.zLen; z++) {
                this.cache[x * this.xLen + z] = world.getChunkSafely(x + this.cx0, z + this.cz0);
            }
        }
    }

    public static ChunkShadowRegion ofWorldPos(World world, int x0, int z0, int x1, int z1) {
        return new ChunkShadowRegion(world, x0 >> 4, z0 >> 4, x1 >> 4, z1 >> 4);
    }

    public WorldChunk getChunkFor(long x, long z) {
        var cx = ChunkPos.ofWorld(x) - this.cx0;
        var cz = ChunkPos.ofWorld(z) - this.cz0;

        if (cx < 0 || cz < 0 || cx > this.xLen || cz > this.zLen) {
            throw new IndexOutOfBoundsException("chunk_pos out of bounds");
        }

        return this.cache[cx * this.xLen + cz];
    }


    public int getXLen() {
        return xLen;
    }

    public int getZLen() {
        return zLen;
    }

    public World getWorld() {
        return world;
    }

    @Override
    public IBlockAccess getBlockAccess(long x, long y, long z) {
        return new ChunkBlockAccess(this.world, x, y, z, getChunkFor(x, z));
    }

    @Override
    public int getBlockId(long x, long y, long z) {
        return getChunkFor(x, z).getBlockId((int) (x & 15), (int) y, (int) (z & 15));
    }

    @Override
    public byte getBlockMetadata(long x, long y, long z) {
        return getChunkFor(x, z).getBlockMeta((int) (x & 15), (int) y, (int) (z & 15));
    }

    @Override
    public byte getBlockLight(long x, long y, long z) {
        return getChunkFor(x, z).getBlockLight((int) (x & 15), (int) y, (int) (z & 15));
    }
}
