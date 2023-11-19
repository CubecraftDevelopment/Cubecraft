package net.cubecraft.world.entity;

import net.cubecraft.world.IWorld;
import net.cubecraft.world.item.ItemStack;

public abstract class EntityItem extends Entity {
    public ItemStack stack;

    public EntityItem(IWorld world) {
        super(world);
    }
}
