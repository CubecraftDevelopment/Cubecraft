package net.cubecraft.world.worldGen;

import net.cubecraft.world.chunk.WorldChunk;
import net.cubecraft.world.chunk.pos.ChunkPos;

public interface IChunkProvider {
    WorldChunk loadChunk(ChunkPos pos);
}
