package net.cubecraft.world.environment;

import net.cubecraft.world.World;
import net.cubecraft.world.block.blocks.Blocks;

public final class OverworldEnvironment implements Environment {

    @Override
    public int getBlockID(World world, long x, long y, long z) {
        if (y < 0) {
            return Blocks.BEDROCK.getId();
        }
        if (y < 110) {
            return Blocks.STONE.getId();
        }
        if (y < 128) {
            return Blocks.WATER.getId();
        }

        return Blocks.AIR.getId();
    }

    @Override
    public byte getBlockLight(World world, long x, long y, long z) {
        return 127;
    }

    @Override
    public String getName() {
        return "cubecraft:overworld";
    }
}
