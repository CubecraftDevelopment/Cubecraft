package net.cubecraft.world.item.behavior;

import net.cubecraft.world.block.access.IBlockAccess;
import net.cubecraft.world.item.Item;

public class BlockItemBehavior implements ItemBehavior {
    @Override
    public void onUse(Item item, IBlockAccess block) {
        block.setBlockID(item.getId(), true);
    }

    @Override
    public void onDig(Item item, IBlockAccess block) {
        block.setBlockID("cubecraft:air", true);
    }
}
