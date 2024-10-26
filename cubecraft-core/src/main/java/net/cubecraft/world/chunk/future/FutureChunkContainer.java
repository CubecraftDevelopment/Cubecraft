package net.cubecraft.world.chunk.future;

import net.cubecraft.world.World;
import net.cubecraft.world.chunk.Chunk;
import net.cubecraft.world.chunk.pos.ChunkPos;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public final class FutureChunkContainer implements ChunkFuture {
    private final World world;
    private final ChunkPos pos;

    public FutureChunkContainer(World world, ChunkPos pos) {
        this.world = world;
        this.pos = pos;
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return false;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public boolean isDone() {
        return this.world.isChunkLoaded(this.pos);
    }

    @Override
    public Chunk get(){
        this.world.waitUntilChunkExist(pos.getX(), pos.getZ());
        return this.world.getChunk(this.pos);
    }

    @Override
    public Chunk get(long timeout, @NotNull TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return get();
    }
}
