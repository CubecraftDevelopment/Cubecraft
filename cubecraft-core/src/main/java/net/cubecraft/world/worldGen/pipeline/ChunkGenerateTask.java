package net.cubecraft.world.worldGen.pipeline;

import net.cubecraft.world.chunk.ProviderChunk;
import me.gb2022.commons.container.ArrayQueue;

import java.util.List;

public final class ChunkGenerateTask {
    private final ChunkGeneratorPipeline pipeline;
    private final ProviderChunk chunk;
    private final ChunkGenerateContext context;

    public ChunkGenerateTask(ChunkGeneratorPipeline pipeline, ProviderChunk chunk) {
        this.pipeline = pipeline;
        this.chunk = chunk;
        this.context = new ChunkGenerateContext(chunk, pipeline);
    }

    public static ChunkGenerateTask createTask(ChunkGeneratorPipeline pipeline, ProviderChunk chunk) {
        return new ChunkGenerateTask(pipeline, chunk);
    }

    public static Runnable createTask(ChunkGeneratorPipeline pipeline, ProviderChunk chunk, ArrayQueue<ProviderChunk> dest) {
        return () -> {
            ChunkGenerateTask t = createTask(pipeline, chunk);
            dest.add(t.generateChunk());
        };
    }

    public ProviderChunk getChunk() {
        return chunk;
    }

    public ProviderChunk generateChunk() {
        List<TerrainGeneratorHandler> handlers = this.pipeline.getHandlerList();
        for (TerrainGeneratorHandler handler : handlers) {
            handler.generate(this.context);
        }
        return this.context.getChunk();
    }
}
