package net.cubecraft.world.chunk.future;

import net.cubecraft.world.chunk.Chunk;
import net.cubecraft.world.chunk.ChunkState;
import net.cubecraft.world.chunk.WorldChunk;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public final class CompleteChunkContainer implements ChunkFuture {
    private final WorldChunk chunk;

    public CompleteChunkContainer(WorldChunk chunk) {
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
        return this.chunk.getState() == ChunkState.COMPLETE;
    }

    @Override
    public Chunk get() {
        while (!this.isDone()) {
            Thread.onSpinWait();
            try {
                Thread.sleep(8);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            Thread.yield();
        }
        return this.chunk;
    }

    @Override
    public Chunk get(long timeout, @NotNull TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return get();
    }
}
