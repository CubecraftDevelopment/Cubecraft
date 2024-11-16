package net.cubecraft.world.dump;

import net.cubecraft.world.BlockAccessor;
import net.cubecraft.world.World;
import net.cubecraft.world.block.access.BlockAccess;
import net.cubecraft.world.block.blocks.Blocks;
import net.cubecraft.world.chunk.task.ChunkLoadTicket;

/**
 * A fixed-size region for render chunk compiling
 */
public final class ChunkCompileRegion implements BlockAccessor {
    private final BlockAccess[] buffer = new BlockAccess[18 * 18 * 18];
    public long cx;
    public long cy;
    public long cz;
    private World world;

    public void attach(World world, long cx, long cy, long cz) {
        this.world = world;
        this.cx = cx;
        this.cy = cy;
        this.cz = cz;
    }

    public boolean dump(World world, long cx, long cy, long cz) {
        attach(world, cx, cy, cz);

        var empty = true;

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                world.loadChunk((int) (cx + i - 1), (int) (cz + j - 1), ChunkLoadTicket.LOAD_DATA);
            }
        }

        for (int x = -1; x < 17; x++) {
            for (int z = -1; z < 17; z++) {
                for (int y = -1; y < 17; ++y) {
                    var block = DumpedBlockAccess.dump(world, cx * 16L + x, cy * 16L + y, cz * 16L + z);

                    this.buffer[(x + 1) * 18 * 18 + (y + 1) * 18 + z + 1] = block;

                    if (block.getBlockId() == Blocks.AIR.getId()) {
                        continue;
                    }

                    empty = false;
                }
            }
        }

        return empty;
    }

    @Override
    public BlockAccess getBlockAccess(long x, long y, long z) {
        int rx = (int) (x + 1 - this.cx * 16);
        int ry = (int) (y + 1 - this.cy * 16);
        int rz = (int) (z + 1 - this.cz * 16);

        if (rx < 0 || rx > 17 || ry < 0 || ry > 17 || rz < 0 || rz > 17) {
            throw new IllegalArgumentException("%s/%s/%s".formatted(rx, ry, rz));
        }

        return this.buffer[rx * 18 * 18 + ry * 18 + rz];
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
