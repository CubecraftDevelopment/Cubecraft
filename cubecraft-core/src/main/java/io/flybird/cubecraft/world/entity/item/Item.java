package io.flybird.cubecraft.world.entity.item;

import io.flybird.cubecraft.world.IWorld;
import io.flybird.cubecraft.world.entity.Entity;
import io.flybird.cubecraft.world.item.ItemStack;
import org.joml.Vector3d;

public abstract class Item extends Entity {
    public ItemStack stack;

    public Item(IWorld world) {
        super(world);
    }
}
