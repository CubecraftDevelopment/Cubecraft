package net.cubecraft.world.block.property;

import me.gb2022.commons.math.AABB;
import me.gb2022.commons.math.hitting.HitBox;
import net.cubecraft.world.block.Block;
import net.cubecraft.world.block.access.IBlockAccess;
import net.cubecraft.world.block.property.collision.CollisionProperty;
import net.cubecraft.world.block.property.hitbox.HitBoxProperty;

import java.util.Collection;
import java.util.List;

public interface BlockPropertyDispatcher {
    static Collection<AABB> getCollisionBox(IBlockAccess block) {
        Block b = block.getBlock();
        if (b == null) {
            return List.of();
        }

        CollisionProperty property = b.getBlockProperty("cubecraft:collision", CollisionProperty.class);
        if (property == null) {
            return List.of();
        }
        return property.get(block);
    }

    static Collection<HitBox> getHitBox(IBlockAccess block) {
        Block b = block.getBlock();
        if (b == null) {
            return List.of();
        }

        HitBoxProperty property = b.getBlockProperty("cubecraft:hitbox", HitBoxProperty.class);
        if (property == null) {
            return List.of();
        }
        return property.get(block);
    }

    static boolean isSolid(IBlockAccess block) {
        if (block.getBlock() == null) {
            return false;
        }
        return block.getBlock().isSolid();
    }

    static Collection<AABB> getCollisionBox(Block block, IBlockAccess access) {
        if (block == null) {
            return List.of();
        }
        CollisionProperty property = block.getBlockProperty("cubecraft:collision", CollisionProperty.class);
        if (property == null) {
            return List.of();
        }
        return property.get(access);
    }
}
