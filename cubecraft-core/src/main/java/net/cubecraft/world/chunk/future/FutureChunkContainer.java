package net.cubecraft.world.chunk.future;

import net.cubecraft.world.IWorld;
import net.cubecraft.world.chunk.Chunk;
import net.cubecraft.world.chunk.pos.ChunkPos;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class FutureChunkContainer implements ChunkFuture {
    private final IWorld world;
    private final ChunkPos pos;

    public FutureChunkContainer(IWorld world, ChunkPos pos) {
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
    public Chunk get() throws InterruptedException, ExecutionException {
        this.world.waitUntilChunkExist(pos);
        return this.world.getChunk(this.pos);
    }

    @Override
    public Chunk get(long timeout, @NotNull TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return get();
    }
}
