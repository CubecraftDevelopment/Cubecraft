package net.cubecraft.world.block.behavior;

import net.cubecraft.internal.block.BlockType;
import net.cubecraft.world.block.EnumFacing;
import net.cubecraft.world.block.access.ChunkBlockAccess;
import net.cubecraft.world.block.access.IBlockAccess;
import ink.flybird.fcommon.registry.TypeItem;

import java.util.Objects;

@TypeItem("gravity_block")
public final class GravityBlockBehavior implements BlockBehavior {
    @Override
    public void onBlockTick(IBlockAccess block) {
        IBlockAccess blockAccess = block.getNear(EnumFacing.Down);
        if (block instanceof ChunkBlockAccess cBlock) {
            if (!blockAccess.getBlock().isLiquid() || !Objects.equals(blockAccess.getBlockID(), BlockType.AIR)) {
                return;
            }
            cBlock.setBlockID(BlockType.AIR, true);
        }
    }
}
