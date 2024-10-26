package net.cubecraft.world.entity;

import net.cubecraft.world.World;
import net.cubecraft.world.item.ItemStack;

public abstract class EntityItem extends Entity {
    public ItemStack stack;

    public EntityItem(World world) {
        super(world);
    }
}
