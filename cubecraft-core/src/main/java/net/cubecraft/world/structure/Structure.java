package net.cubecraft.world.structure;

import net.cubecraft.world.block.EnumFacing;
import org.joml.Vector3i;

import java.util.Random;

public interface Structure {
    void generate(StructureGenerator generator, Random rand, long x, long y, long z, EnumFacing facing);

    Vector3i getSize();
}
