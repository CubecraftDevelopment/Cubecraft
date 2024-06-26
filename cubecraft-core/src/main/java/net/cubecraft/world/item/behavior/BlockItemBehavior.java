package net.cubecraft.world.item.behavior;

import me.gb2022.commons.math.hitting.HitResult;
import net.cubecraft.ContentRegistries;
import net.cubecraft.world.block.EnumFacing;
import net.cubecraft.world.block.access.IBlockAccess;
import net.cubecraft.world.block.property.BlockPropertyDispatcher;
import net.cubecraft.world.item.Item;

public class BlockItemBehavior implements ItemBehavior {
    @Override
    public void onUse(HitResult result, Item item, IBlockAccess block) {
        IBlockAccess tgt=block.getNear(EnumFacing.fromId(result.getHitFace()));
        if(!tgt.getWorld().isFree(BlockPropertyDispatcher.getCollisionBox(ContentRegistries.BLOCK.get(item.getId()),tgt))){
            return;
        }
        tgt.setBlockID(item.getId(), true);
    }

    @Override
    public void onDig(HitResult result, Item item, IBlockAccess block) {
        block.setBlockID("cubecraft:air", true);
    }
}
