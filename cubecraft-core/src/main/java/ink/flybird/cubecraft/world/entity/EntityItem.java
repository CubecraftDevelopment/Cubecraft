package ink.flybird.cubecraft.world.entity;

import ink.flybird.cubecraft.world.IWorld;
import ink.flybird.cubecraft.world.item.ItemStack;
import ink.flybird.cubecraft.world.entity.Entity;

public abstract class EntityItem extends Entity {
    public ItemStack stack;

    public EntityItem(IWorld world) {
        super(world);
    }
}
