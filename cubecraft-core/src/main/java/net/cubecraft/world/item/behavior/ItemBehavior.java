package net.cubecraft.world.item.behavior;

import me.gb2022.commons.math.hitting.HitResult;
import net.cubecraft.world.block.access.IBlockAccess;
import net.cubecraft.world.entity.Entity;
import net.cubecraft.world.item.Item;

public interface ItemBehavior {
    default void onAttack(HitResult result, Item item, Entity entity) {
    }

    default void onUse(HitResult result, Item item, Entity entity) {
    }

    default void onUse(HitResult result, Item item, IBlockAccess block) {
    }

    default void onDig(HitResult result, Item item, IBlockAccess block) {
    }
}
