package net.cubecraft.world.dump;

import net.cubecraft.world.BlockAccessor;
import net.cubecraft.world.World;
import net.cubecraft.world.block.access.BlockAccess;

public final class SnapshotRegion implements BlockAccessor {
    private final World world;
    private final long x;
    private final long y;
    private final long z;
    private final int width;
    private final int height;
    private final int depth;

    private final BlockAccess[] data;

    public SnapshotRegion(World world, long x0, long y0, long z0, long x1, long y1, long z1) {
        this.world = world;
        this.x = x0;
        this.y = y0;
        this.z = z0;
        this.width = (int) (x1 - x0);
        this.height = (int) (y1 - y0);
        this.depth = (int) (z1 - z0);

        this.data = new BlockAccess[this.width * this.height * this.depth];

        for (var x = x0; x <= x1; x++) {
            for (var y = y0; y <= y1; y++) {
                for (var z = z0; z <= z1; z++) {
                    this.data[pos(x, y, z)] = world.getBlockAccess(x, y, z);
                }
            }
        }
    }

    private int pos(long x, long y, long z) {
        if (x < this.x || x > this.x + this.width || y < this.y || y > this.y + this.height || z < this.z || z > this.z + this.depth) {
            throw new IndexOutOfBoundsException();
        }
        return (int) (x * this.width * this.height + y * this.height + z);
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

    public World getWorld() {
        return world;
    }

    public BlockAccess[] getData() {
        return data;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getDepth() {
        return depth;
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
}
