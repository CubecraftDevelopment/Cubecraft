package net.cubecraft.world.dump;

import net.cubecraft.world.BlockAccessor;
import net.cubecraft.world.World;
import net.cubecraft.world.block.access.IBlockAccess;
import net.cubecraft.world.block.blocks.Blocks;
import net.cubecraft.world.chunk.task.ChunkLoadTicket;

/**
 * A fixed-size region for render chunk compiling
 */
public final class ChunkCompileRegion implements BlockAccessor {
    public final long cx;
    public final long cy;
    public final long cz;

    private final World world;
    private final IBlockAccess[] data;

    public ChunkCompileRegion(World world, int cx, int cy, int cz, IBlockAccess[] data) {
        this.world = world;

        this.cx = cx;
        this.cy = cy;
        this.cz = cz;

        this.data = data;
    }

    public static ChunkCompileRegion read(World world, int cx, int cy, int cz) {
        var data = new IBlockAccess[18 * 18 * 18];
        var empty = true;

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                world.loadChunk(cx + i - 1, cz + j - 1, ChunkLoadTicket.LOAD_DATA);
            }
        }

        for (int x = -1; x < 17; x++) {
            for (int z = -1; z < 17; z++) {
                for (int y = -1; y < 17; ++y) {
                    var block = world.getBlockAccess(x + cx * 16L, y + cy * 16L, z + cz * 16L);

                    data[(x + 1) * 18 * 18 + (y + 1) * 18 + z + 1] = block;

                    if (block.getBlockId() == Blocks.AIR.getId()) {
                        continue;
                    }

                    empty = false;
                }
            }
        }

        if (empty) {
            return null;
        }
        return new ChunkCompileRegion(world, cx, cy, cz, data);
    }


    @Override
    public IBlockAccess getBlockAccess(long x, long y, long z) {
        int rx = (int) (x + 1 - this.cx * 16);
        int ry = (int) (y + 1 - this.cy * 16);
        int rz = (int) (z + 1 - this.cz * 16);

        if (rx < 0 || rx > 17 || ry < 0 || ry > 17 || rz < 0 || rz > 17) {
            throw new IllegalArgumentException("%s/%s/%s".formatted(rx, ry, rz));
        }

        return this.data[rx * 18 * 18 + ry * 18 + rz];
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
}
