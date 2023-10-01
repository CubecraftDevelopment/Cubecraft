package ink.flybird.cubecraft.world.block.controller;

import ink.flybird.cubecraft.world.block.access.IBlockAccess;

public interface BlockController {
    default void onBlockTick(IBlockAccess block) {
    }

    default void onBlockRandomTick(IBlockAccess block) {
    }

    default void onBlockPlace(IBlockAccess block) {
    }

    default void onBlockDestroy(IBlockAccess block){
    }
}
