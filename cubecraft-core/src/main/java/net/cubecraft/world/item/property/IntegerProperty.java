package net.cubecraft.world.item.property;

import net.cubecraft.world.item.ItemStack;

public class IntegerProperty implements ItemProperty<Integer> {
    private final int value;

    public IntegerProperty(int value) {
        this.value = value;
    }

    @Override
    public Integer get(ItemStack stack) {
        return this.value;
    }
}
