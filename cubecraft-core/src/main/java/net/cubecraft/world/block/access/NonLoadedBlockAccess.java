package net.cubecraft.world.block.access;

import net.cubecraft.world.IWorld;
import net.cubecraft.world.block.Block;
import net.cubecraft.world.block.EnumFacing;
import net.cubecraft.world.block.blocks.BlockRegistry;

public class NonLoadedBlockAccess extends IBlockAccess {
    public NonLoadedBlockAccess(IWorld world, long x, long y, long z) {
        super(world, x, y, z);
    }

    @Override
    public String getBlockID() {
        return "cubecraft:air";
    }

    @Override
    public byte getBlockMeta() {
        return 0;
    }

    @Override
    public byte getBlockLight() {
        return 0;
    }

    @Override
    public EnumFacing getBlockFacing() {
        return EnumFacing.Up;
    }

    @Override
    public String getBiome() {
        return "cubecraft:plains";
    }

    @Override
    public Block getBlock() {
        return BlockRegistry.AIR;
    }
}
