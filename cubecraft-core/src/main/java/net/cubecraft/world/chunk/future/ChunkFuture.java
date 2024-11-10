package net.cubecraft.world.chunk.future;

import net.cubecraft.world.chunk.Chunk;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public interface ChunkFuture<V extends Chunk> extends Future<V> {

    @Override
    V get();
}
