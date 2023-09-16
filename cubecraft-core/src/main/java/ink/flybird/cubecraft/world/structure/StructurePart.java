package ink.flybird.cubecraft.world.structure;

import ink.flybird.cubecraft.world.block.EnumFacing;

import java.util.Random;

public interface StructurePart{
    void generate(Structure s, StructureGenerator context, Random rand, long x, long y, long z, EnumFacing facing);
}
