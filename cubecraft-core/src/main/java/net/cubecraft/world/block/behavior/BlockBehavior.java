package net.cubecraft.world.block.behavior;

import net.cubecraft.world.block.access.IBlockAccess;

public interface BlockBehavior {
    default void onBlockTick(IBlockAccess block) {
    }

    default void onBlockRandomTick(IBlockAccess block) {
    }

    default void onBlockPlace(IBlockAccess block) {
    }

    default void onBlockDestroy(IBlockAccess block){
    }
}
