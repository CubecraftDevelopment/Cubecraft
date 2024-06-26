package net.cubecraft.internal.item;

import me.gb2022.commons.event.EventHandler;
import me.gb2022.commons.registry.FieldRegistry;
import me.gb2022.commons.registry.FieldRegistryHolder;
import net.cubecraft.ContentRegistries;
import net.cubecraft.event.register.BlockRegisterEvent;
import net.cubecraft.world.item.EmptyItem;
import net.cubecraft.world.item.Item;

@FieldRegistryHolder("cubecraft")
public interface ItemRegistry {
    @FieldRegistry("cubecraft:__OFFHAND__")
    Item DUMMY = new EmptyItem("cubecraft:__OFFHAND__");

    @EventHandler
    static void onBlockRegister(BlockRegisterEvent event) {
        ContentRegistries.ITEM.registerItem(event.getId(), new BlockItem(event.getId()));
    }
}
