package net.cubecraft.world.dimension;

import net.cubecraft.internal.block.BlockType;
import net.cubecraft.world.IWorld;
import net.cubecraft.world.chunk.Chunk;

public final class DimensionOverworld implements Dimension {

    @Override
    public String predictBlockID(IWorld world, long x, long y, long z) {
        if (Dimension.outsideWorld(x, z)) {
            return BlockType.STONE;
        }

        if (y >= Chunk.HEIGHT) {
            return BlockType.AIR;
        }
        if (y < 0) {
            return BlockType.STONE;
        }

        return null;
    }
}
