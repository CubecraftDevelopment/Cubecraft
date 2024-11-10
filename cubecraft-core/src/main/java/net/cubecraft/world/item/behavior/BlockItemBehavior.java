package net.cubecraft.world.item.behavior;

import me.gb2022.commons.math.hitting.HitResult;
import net.cubecraft.world.block.EnumFacing;
import net.cubecraft.world.block.access.BlockAccess;
import net.cubecraft.world.block.access.IBlockAccess;
import net.cubecraft.world.block.blocks.Blocks;
import net.cubecraft.world.block.property.BlockPropertyDispatcher;
import net.cubecraft.world.item.Item;

public class BlockItemBehavior extends DigableItemBehavior {
    @Override
    public void onUse(HitResult result, Item item, BlockAccess block) {
        IBlockAccess tgt = block.getNear(EnumFacing.fromId(result.getHitFace()));
        if (!tgt.getWorld().isFree(BlockPropertyDispatcher.getCollisionBox(Blocks.REGISTRY.object(item.getId()), tgt))) {
            return;
        }
        tgt.setBlockId(Blocks.REGISTRY.id(item.getId()), false);
    }
}
