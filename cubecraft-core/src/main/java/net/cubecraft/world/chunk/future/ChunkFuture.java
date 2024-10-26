package net.cubecraft.world.chunk.future;

import net.cubecraft.world.chunk.Chunk;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public interface ChunkFuture extends Future<Chunk> {

    @Override
    Chunk get();
}
