package ink.flybird.cubecraft.world.block.property;

import ink.flybird.cubecraft.world.block.access.IBlockAccess;

public interface BlockProperty<I> {
    I get(IBlockAccess blockAccess);
}
