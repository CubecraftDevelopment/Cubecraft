package net.cubecraft.internal.inventory;

import me.gb2022.commons.registry.TypeItem;
import net.cubecraft.world.item.container.Inventory;

@TypeItem("cubecraft:player")
public class PlayerInventory extends Inventory {
    public static final int HELMET_SLOT = 60;
    public static final int CHESTPLATE_SLOT = 61;
    public static final int LEGGING_SLOT = 62;
    public static final int BOOTS_SLOT = 63;

    public static final int OFF_HAND_SLOT = 36;

    @Override
    protected int getCapacity() {
        return 64;
    }
}
