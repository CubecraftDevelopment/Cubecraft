package net.cubecraft.world.block.property;

import ink.flybird.fcommon.math.AABB;
import ink.flybird.fcommon.math.hitting.HitBox;
import net.cubecraft.world.block.access.IBlockAccess;
import net.cubecraft.world.block.property.collision.CollisionProperty;
import net.cubecraft.world.block.property.hitbox.HitBoxProperty;

import java.util.Collection;
import java.util.List;

public interface BlockPropertyDispatcher {
    static Collection<AABB> getCollisionBox(IBlockAccess block) {
        CollisionProperty property = block.getBlock().getBlockProperty("cubecraft:collision", CollisionProperty.class);
        if (property == null) {
            return List.of();
        }
        return property.get(block);
    }

    static Collection<HitBox> getHitBox(IBlockAccess block) {
        HitBoxProperty property = block.getBlock().getBlockProperty("cubecraft:hitbox", HitBoxProperty.class);
        if (property == null) {
            return List.of();
        }
        return property.get(block);
    }

    static boolean isSolid(IBlockAccess block) {
        return block.getBlock().isSolid();
    }
}
