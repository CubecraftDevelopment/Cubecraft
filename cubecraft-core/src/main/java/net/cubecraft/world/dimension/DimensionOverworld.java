package net.cubecraft.world.dimension;

import net.cubecraft.internal.block.BlockType;
import net.cubecraft.world.World;
import net.cubecraft.world.block.blocks.Blocks;
import net.cubecraft.world.chunk.Chunk;

public final class DimensionOverworld implements Dimension {

    @Override
    public String predictBlockID(World world, long x, long y, long z) {
        if (y >= Chunk.HEIGHT) {
            return BlockType.AIR;
        }

        if (y < 48) {
            return BlockType.STONE;
        }

        if (y < 128) {
            return "cubecraft:calm_water";
        }

        return null;
    }

    @Override
    public int getBlockID(World world, long x, long y, long z) {
        if (y < 48) {
            return Blocks.STONE.getId();
        }
        if (y < 128) {
            return Blocks.CALM_WATER.getId();
        }

        return Blocks.AIR.getId();
    }

    @Override
    public byte getBlockLight(World world, long x, long y, long z) {
        return 0;
    }
}
