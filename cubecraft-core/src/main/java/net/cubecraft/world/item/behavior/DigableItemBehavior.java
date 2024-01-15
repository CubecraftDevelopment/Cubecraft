package net.cubecraft.world.item.behavior;

import ink.flybird.fcommon.math.hitting.HitResult;
import net.cubecraft.world.block.access.IBlockAccess;
import net.cubecraft.world.item.Item;

public class DigableItemBehavior implements ItemBehavior {
    @Override
    public void onDig(HitResult result, Item item, IBlockAccess block) {
        block.setBlockID("cubecraft:air",true);
    }
}
