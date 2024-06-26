package net.cubecraft.world;

import net.cubecraft.world.block.Block;

public interface WorldListener {

    default void onBlockChange(int x, int y, int z, Block block) {}
}
