package net.cubecraft.world.dump;

import net.cubecraft.world.BlockAccessor;
import net.cubecraft.world.World;
import net.cubecraft.world.block.access.BlockAccess;
import net.cubecraft.world.block.blocks.Blocks;
import net.cubecraft.world.chunk.WorldChunk;

public class ThreadedDumpRegion implements BlockAccessor {
    private long x, y, z;
    private int width, height, depth;

    private BlockAccess[] data;

    public boolean dumpWorld(World world, long x0, long y0, long z0, int width, int height, int depth) {
        setRegion(x0, y0, z0, width, height, depth);
        boolean empty = true;

        var cx0 = (int) (x0) >> 4;
        var cx1 = (int) (x0 + width) >> 4;
        var cz0 = (int) (z0) >> 4;
        var cz1 = (int) (z0 + height) >> 4;

        var clx = (cx1 - cx0 + 1);
        var clz = (cz1 - cz0 + 1);

        var chunks = new WorldChunk[clx * clz];

        for (var cx = 0; cx < clx; cx++) {
            for (var cz = 0; cz < clz; cz++) {
                chunks[clx * cx + cz] = world.getChunkSafely(cx + cx0, cz + cz0);
            }
        }

        for (long x = x0; x < x0 + width; x++) {
            for (long y = y0; y < y0 + height; y++) {
                for (long z = z0; z < z0 + depth; z++) {
                    var cx = x >> 4;
                    var cz = z >> 4;

                    var chunk = chunks[(int) ((cx - cx0) * clx + (cz - cz0))];

                    int id;
                    byte meta;
                    byte light;

                    if (y < 0 || y > WorldChunk.HEIGHT) {
                        id = world.getBlockId(x, y, z);
                        meta = world.getBlockMetadata(x, y, z);
                        light = world.getBlockLight(x, y, z);
                    } else {
                        id = chunk.getBlockId((int) (x & 15), (int) y, (int) (z & 15));
                        meta = chunk.getBlockMeta((int) (x & 15), (int) y, (int) (z & 15));
                        light = chunk.getBlockLight((int) (x & 15), (int) y, (int) (z & 15));
                    }

                    this.data[pos(x, y, z)] = new DumpedBlockAccess(this, x, y, z, id, meta, light);

                    if (id != Blocks.AIR.getId()) {
                        empty = false;
                    }
                }
            }
        }

        return empty;
    }

    protected void setRegion(long x0, long y0, long z0, int width, int height, int depth) {
        this.x = x0;
        this.y = y0;
        this.z = z0;

        this.width = width;
        this.height = height;
        this.depth = depth;

        this.data = new BlockAccess[this.width * this.height * this.depth];
    }

    public boolean dump(BlockAccessor accessor, long x0, long y0, long z0, int width, int height, int depth) {
        setRegion(x0, y0, z0, width, height, depth);
        var empty = true;

        for (long x = x0; x < x0 + width; x++) {
            for (long y = y0; y < y0 + height; y++) {
                for (long z = z0; z < z0 + depth; z++) {
                    var block = accessor.getBlockAccess(x, y, z);

                    this.data[pos(x, y, z)] = block;

                    if (block.getBlockId() == Blocks.AIR.getId()) {
                        continue;
                    }

                    empty = false;
                }
            }
        }

        return empty;
    }

    private int pos(long x, long y, long z) {
        if (x < this.x || x > this.x + this.width || y < this.y || y > this.y + this.height || z < this.z || z > this.z + this.depth) {
            throw new IndexOutOfBoundsException("%s/%s/%s".formatted(x, y, z));
        }

        var dx = x - this.x;
        var dy = y - this.y;
        var dz = z - this.z;

        return (int) (dx * this.width * this.height + dy * this.height + dz);
    }

    @Override
    public BlockAccess getBlockAccess(long x, long y, long z) {
        return this.data[pos(x, y, z)];
    }

    @Override
    public int getBlockId(long x, long y, long z) {
        return getBlockAccess(x, y, z).getBlockId();
    }

    @Override
    public byte getBlockMetadata(long x, long y, long z) {
        return getBlockAccess(x, y, z).getBlockMeta();
    }

    @Override
    public byte getBlockLight(long x, long y, long z) {
        return getBlockAccess(x, y, z).getBlockLight();
    }
}
