package net.cubecraft.event.block;

import net.cubecraft.world.World;
import net.cubecraft.world.block.BlockState;

@Deprecated
public record BlockChangeEvent(World world, long x, long y, long z, BlockState newBlockState, BlockState oldState){}
