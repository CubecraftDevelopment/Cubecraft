package net.cubecraft.world.block.behavior;

import me.gb2022.commons.registry.TypeItem;
import net.cubecraft.world.block.EnumFacing;
import net.cubecraft.world.block.access.BlockAccess;
import net.cubecraft.world.block.access.ChunkBlockAccess;
import net.cubecraft.world.block.blocks.Blocks;

import java.util.Objects;

@TypeItem("gravity_block")
public final class GravityBlockBehavior implements BlockBehavior {
    @Override
    public void onBlockTick(BlockAccess block) {
        BlockAccess blockAccess = block.getNear(EnumFacing.Down);
        if (block instanceof ChunkBlockAccess cBlock) {
            if (!blockAccess.getBlock().isLiquid() || !Objects.equals(blockAccess.getBlockId(), Blocks.AIR.getId())) {
                return;
            }
            cBlock.setBlockId(Blocks.AIR.getId(), true);
        }
    }
}
