package net.cubecraft.world.item.behavior;

import net.cubecraft.world.block.access.IBlockAccess;
import net.cubecraft.world.entity.Entity;
import net.cubecraft.world.item.Item;

public interface ItemBehavior {
    default void onDig(Item item, IBlockAccess block) {
    }

    default void onUse(Item item, IBlockAccess block) {
    }

    default void onAttack(Item item, Entity entity) {
    }

    default void onUse(Item item, Entity entity) {
    }
}
