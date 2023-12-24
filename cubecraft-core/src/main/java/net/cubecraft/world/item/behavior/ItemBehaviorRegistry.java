package net.cubecraft.world.item.behavior;

import ink.flybird.fcommon.registry.FieldRegistry;
import ink.flybird.fcommon.registry.FieldRegistryHolder;

@FieldRegistryHolder("cubecraft")
public interface ItemBehaviorRegistry {
    @FieldRegistry("block")
    ItemBehavior BLOCK = new BlockItemBehavior();

    @FieldRegistry("diggable")
    ItemBehavior DIGGABLE = new DigableItemBehavior();
}
