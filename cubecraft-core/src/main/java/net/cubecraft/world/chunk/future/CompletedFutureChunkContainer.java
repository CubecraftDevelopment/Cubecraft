package net.cubecraft.world.chunk.future;

import net.cubecraft.world.chunk.Chunk;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class CompletedFutureChunkContainer implements ChunkFuture {
    private final Chunk chunk;

    public CompletedFutureChunkContainer(Chunk chunk) {
        this.chunk = chunk;
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
        return true;
    }

    @Override
    public Chunk get() throws InterruptedException, ExecutionException {
        return this.chunk;
    }

    @Override
    public Chunk get(long timeout, @NotNull TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return get();
    }
}
