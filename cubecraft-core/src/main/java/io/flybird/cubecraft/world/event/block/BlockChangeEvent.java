package io.flybird.cubecraft.world.event.block;

import io.flybird.cubecraft.world.IWorld;
import io.flybird.cubecraft.world.block.BlockState;

@Deprecated
public record BlockChangeEvent(IWorld world, long x, long y, long z, BlockState newBlockState,BlockState oldState){}
