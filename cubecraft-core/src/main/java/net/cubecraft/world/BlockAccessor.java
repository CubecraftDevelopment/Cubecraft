package net.cubecraft.world;

import net.cubecraft.util.register.Registered;
import net.cubecraft.world.block.Block;
import net.cubecraft.world.block.access.BlockAccess;
import net.cubecraft.world.block.access.NonLoadedBlockAccess;
import net.cubecraft.world.block.blocks.Blocks;

public interface BlockAccessor {
    BlockAccessor VIRTUAL = new BlockAccessor() {
        @Override
        public BlockAccess getBlockAccess(long x, long y, long z) {
            return new NonLoadedBlockAccess(null, x, y, z);
        }

        @Override
        public int getBlockId(long x, long y, long z) {
            return 0;
        }

        @Override
        public byte getBlockMetadata(long x, long y, long z) {
            return 0;
        }

        @Override
        public byte getBlockLight(long x, long y, long z) {
            return 0;
        }
    };

    BlockAccess getBlockAccess(long x, long y, long z);

    int getBlockId(long x, long y, long z);

    byte getBlockMetadata(long x, long y, long z);

    byte getBlockLight(long x, long y, long z);

    default Registered<Block> getBlock(long x, long y, long z) {
        return Blocks.REGISTRY.registered(getBlockId(x, y, z));
    }

    default void setBlockId(long x, long y, long z, int i, boolean silent) {
    }
}
