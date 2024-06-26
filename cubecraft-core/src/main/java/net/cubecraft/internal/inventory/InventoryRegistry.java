package net.cubecraft.internal.inventory;

import me.gb2022.commons.registry.ConstructingMap;
import me.gb2022.commons.registry.ItemRegisterFunc;
import net.cubecraft.world.item.container.Inventory;


public class InventoryRegistry {
    @ItemRegisterFunc(Inventory.class)
    public void reg(ConstructingMap<Inventory> map){
        map.registerItem(InventoryType.PLAYER, PlayerInventory.class);
    }
}
