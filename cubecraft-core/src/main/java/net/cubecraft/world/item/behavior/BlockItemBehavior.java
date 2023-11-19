package net.cubecraft.world.item.behavior;

import net.cubecraft.world.block.access.IBlockAccess;
import net.cubecraft.world.item.Item;

public class BlockItemBehavior implements ItemBehavior{
    //todo
    @Override
    public void onUse(Item item,IBlockAccess block) {
        block.setBlockID("cubecraft:stone",false);
    }
}
