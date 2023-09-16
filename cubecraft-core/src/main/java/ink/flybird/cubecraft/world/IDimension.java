package ink.flybird.cubecraft.world;

import ink.flybird.cubecraft.world.block.BlockState;

public interface IDimension {
    BlockState predictBlockAt(IWorld world, long x, long y, long z);

    byte predictLightAt(IWorld world, long x, long y, long z);

    String predictBlockID(IWorld world, long x, long y, long z);

    byte predictBlockMetaAt(IWorld world, long x, long y, long z);
}