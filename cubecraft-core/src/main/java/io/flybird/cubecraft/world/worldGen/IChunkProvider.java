package io.flybird.cubecraft.world.worldGen;

import io.flybird.cubecraft.world.chunk.WorldChunk;
import io.flybird.cubecraft.world.chunk.ChunkPos;

public interface IChunkProvider {
    WorldChunk loadChunk(ChunkPos pos);
}
