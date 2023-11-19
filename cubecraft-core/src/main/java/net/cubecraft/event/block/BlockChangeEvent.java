package net.cubecraft.event.block;

import net.cubecraft.world.IWorld;
import net.cubecraft.world.block.BlockState;

@Deprecated
public record BlockChangeEvent(IWorld world, long x, long y, long z, BlockState newBlockState, BlockState oldState){}
