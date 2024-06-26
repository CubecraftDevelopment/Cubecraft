package net.cubecraft.world.item.behavior;

import me.gb2022.commons.registry.FieldRegistry;
import me.gb2022.commons.registry.FieldRegistryHolder;

@FieldRegistryHolder("cubecraft")
public interface ItemBehaviorRegistry {
    @FieldRegistry("block")
    ItemBehavior BLOCK = new BlockItemBehavior();

    @FieldRegistry("diggable")
    ItemBehavior DIGGABLE = new DigableItemBehavior();
}
