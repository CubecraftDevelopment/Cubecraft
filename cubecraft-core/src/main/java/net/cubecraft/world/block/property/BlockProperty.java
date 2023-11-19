package net.cubecraft.world.block.property;

import net.cubecraft.world.block.access.IBlockAccess;

public interface BlockProperty<I> {
    I get(IBlockAccess blockAccess);
}
