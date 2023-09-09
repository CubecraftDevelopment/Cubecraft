package io.flybird.cubecraft.internal.inventory;

import io.flybird.cubecraft.world.item.Inventory;
import ink.flybird.fcommon.registry.TypeItem;

@TypeItem("cubecraft:player")
public class PlayerInventory extends Inventory {
    public PlayerInventory() {
        super(36);
    }
}
