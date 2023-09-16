package ink.flybird.cubecraft.world.worldGen;

import ink.flybird.cubecraft.world.chunk.WorldChunk;
import ink.flybird.cubecraft.world.chunk.ChunkPos;

public interface IChunkProvider {
    WorldChunk loadChunk(ChunkPos pos);
}
