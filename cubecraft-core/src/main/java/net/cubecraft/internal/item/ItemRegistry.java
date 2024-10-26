package net.cubecraft.internal.item;

import me.gb2022.commons.registry.FieldRegistry;
import me.gb2022.commons.registry.FieldRegistryHolder;
import net.cubecraft.world.item.EmptyItem;
import net.cubecraft.world.item.Item;

@FieldRegistryHolder("cubecraft")
public interface ItemRegistry {
    @FieldRegistry("cubecraft:__OFFHAND__")
    Item DUMMY = new EmptyItem("cubecraft:__OFFHAND__");
}
