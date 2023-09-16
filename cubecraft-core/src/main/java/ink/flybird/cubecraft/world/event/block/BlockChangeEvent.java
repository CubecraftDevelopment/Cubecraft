package ink.flybird.cubecraft.world.event.block;

import ink.flybird.cubecraft.world.IWorld;
import ink.flybird.cubecraft.world.block.BlockState;

@Deprecated
public record BlockChangeEvent(IWorld world, long x, long y, long z, BlockState newBlockState, BlockState oldState){}
