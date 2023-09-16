package ink.flybird.cubecraft.world.entity.item;

import ink.flybird.cubecraft.world.IWorld;
import ink.flybird.cubecraft.world.item.ItemStack;
import ink.flybird.cubecraft.world.entity.Entity;

public abstract class Item extends Entity {
    public ItemStack stack;

    public Item(IWorld world) {
        super(world);
    }
}
