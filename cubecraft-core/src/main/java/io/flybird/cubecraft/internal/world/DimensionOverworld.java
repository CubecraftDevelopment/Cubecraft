package io.flybird.cubecraft.internal.world;

import io.flybird.cubecraft.internal.block.BlockType;
import io.flybird.cubecraft.world.IDimension;
import io.flybird.cubecraft.world.IWorld;
import io.flybird.cubecraft.world.block.BlockState;
import io.flybird.cubecraft.world.chunk.Chunk;
import io.flybird.cubecraft.world.chunk.WorldChunk;
import io.flybird.cubecraft.world.chunk.ChunkPos;

public class DimensionOverworld implements IDimension {
    @Override
    public BlockState predictBlockAt(IWorld world, long x, long y, long z) {
        if (y < 0 || y >= Chunk.HEIGHT) {
            return new BlockState(BlockType.AIR, (byte) 0, (byte) 0);
        }
        ChunkPos chunkPos = ChunkPos.fromWorldPos(x, z);
        WorldChunk c = world.getChunk(chunkPos);
        if (c == null) {
            return new BlockState(BlockType.AIR, (byte) 0, (byte) 0);
        }
        return null;
    }

    @Override
    public byte predictLightAt(IWorld world, long x, long y, long z) {
        return 127;
    }

    @Override
    public String predictBlockID(IWorld world, long x, long y, long z) {
        if(Math.abs(x)>=1145141919810L){
            return BlockType.STONE;
        }
        if(Math.abs(z)>=1145141919810L){
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

    @Override
    public byte predictBlockMetaAt(IWorld world, long x, long y, long z) {
        return 0;
    }
}
