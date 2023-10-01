package ink.flybird.cubecraft.world;


import ink.flybird.cubecraft.world.chunk.WorldChunk;

import java.util.concurrent.ExecutorService;

public interface ChunkStorage {
    default void saveAsync(WorldChunk chunk) {
        getAsyncService().submit(() -> save(chunk));
    }

    void save(WorldChunk chunk);

    ExecutorService getAsyncService();
}
