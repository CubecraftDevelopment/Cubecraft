package net.cubecraft.world.item.property;

import net.cubecraft.world.item.ItemStack;

public interface ItemProperty<I> {
    I get(ItemStack stack);
}
