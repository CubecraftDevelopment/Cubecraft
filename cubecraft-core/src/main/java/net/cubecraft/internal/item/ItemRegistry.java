package net.cubecraft.internal.item;

import ink.flybird.fcommon.event.EventHandler;
import net.cubecraft.ContentRegistries;
import net.cubecraft.event.register.BlockRegisterEvent;

public interface ItemRegistry {
    @EventHandler
    static void onBlockRegister(BlockRegisterEvent event) {
        ContentRegistries.ITEM.registerItem(event.getId(), new BlockItem(event.getId()));
    }
}
