package net.cubecraft.world.block.property;

import me.gb2022.commons.math.AABB;
import me.gb2022.commons.math.hitting.HitBox;
import net.cubecraft.world.block.Block;
import net.cubecraft.world.block.access.BlockAccess;
import net.cubecraft.world.block.access.IBlockAccess;
import net.cubecraft.world.block.blocks.Blocks;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

public interface BlockPropertyDispatcher {
    static Collection<HitBox> getHitBox(BlockAccess block) {
        Block b = block.getBlock();
        if (b == null) {
            return List.of();
        }

        return b.getSelection().get(block);
    }

    static Collection<AABB> getCollisionBox(BlockAccess access) {
        return getCollisionBox(access.getBlock(), access);
    }

    static Collection<AABB> getCollisionBox(Block block,BlockAccess access) {
        if (block == null) {
            return List.of();
        }
        return block.getCollision().get(access);
    }

    static boolean isSolid(BlockAccess block) {
        if (Objects.equals(block.getBlockId(), Blocks.AIR.getId())) {
            return false;
        }

        Block b = block.getBlock();
        return b.isSolid();
    }
}
