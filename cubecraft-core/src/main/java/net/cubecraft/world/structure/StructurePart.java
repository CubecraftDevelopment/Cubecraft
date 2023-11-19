package net.cubecraft.world.structure;

import net.cubecraft.world.block.EnumFacing;

import java.util.Random;

public interface StructurePart{
    void generate(Structure s, StructureGenerator context, Random rand, long x, long y, long z, EnumFacing facing);
}
