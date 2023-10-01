package ink.flybird.cubecraft.world;


import ink.flybird.cubecraft.world.chunk.ChunkPos;
import ink.flybird.cubecraft.world.chunk.WorldChunk;

import java.util.concurrent.ExecutorService;

public interface ChunkProvider {
    default void loadAsync(IWorld world, ChunkPos pos){
        getAsyncService().submit(() -> load(world, pos));
    }

    default void load(IWorld world, ChunkPos pos){
        world.setChunk(this.getChunk(world, pos));
    }

    WorldChunk getChunk(IWorld world, ChunkPos pos);

    ExecutorService getAsyncService();
}
