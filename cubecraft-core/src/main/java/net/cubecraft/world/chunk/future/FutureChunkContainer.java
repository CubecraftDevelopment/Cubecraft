package net.cubecraft.world.chunk.future;

import net.cubecraft.world.World;
import net.cubecraft.world.chunk.ChunkState;
import net.cubecraft.world.chunk.WorldChunk;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public final class FutureChunkContainer implements ChunkFuture<WorldChunk> {
    private final World world;
    private final int cx;
    private final int cz;

    public FutureChunkContainer(World world, int cx, int cz) {
        this.world = world;
        this.cx = cx;
        this.cz = cz;
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
        return this.world.isChunkLoaded(this.cx, this.cz);
    }

    @Override
    public WorldChunk get() {
        this.world.waitUntilChunkExist(this.cx, this.cz);
        return this.world.getChunk(this.cx, this.cz, ChunkState.TERRAIN);
    }

    @Override
    public WorldChunk get(long timeout, @NotNull TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return get();
    }
}
