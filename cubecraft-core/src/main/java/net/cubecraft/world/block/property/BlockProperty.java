package net.cubecraft.world.block.property;

import net.cubecraft.world.block.access.BlockAccess;

public interface BlockProperty<I> {
    I get(BlockAccess blockAccess);
}
