package net.cubecraft.world.block.behavior;

import net.cubecraft.world.block.access.BlockAccess;

public interface BlockBehavior {
    default void onBlockTick(BlockAccess block) {
    }

    default void onBlockRandomTick(BlockAccess block) {
    }

    default void onBlockPlace(BlockAccess block) {
    }

    default void onBlockDestroy(BlockAccess block){
    }
}
