package io.flybird.cubecraft.internal.inventory;

import ink.flybird.fcommon.registry.ConstructingMap;
import ink.flybird.fcommon.registry.ItemRegisterFunc;
import io.flybird.cubecraft.world.item.Inventory;


public class InventoryRegistry {
    @ItemRegisterFunc(Inventory.class)
    public void reg(ConstructingMap<Inventory> map){
        map.registerItem(InventoryType.PLAYER, PlayerInventory.class);
    }
}
