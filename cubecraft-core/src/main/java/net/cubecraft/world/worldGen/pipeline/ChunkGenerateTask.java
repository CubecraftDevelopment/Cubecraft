package net.cubecraft.world.worldGen.pipeline;

import net.cubecraft.world.chunk.PrimerChunk;
import net.cubecraft.world.chunk.pos.ChunkPos;

import java.util.ArrayDeque;

public final class ChunkGenerateTask {
    private final ChunkGeneratorPipeline pipeline;
    private final ChunkPos position;

    public ChunkGenerateTask(ChunkGeneratorPipeline pipeline, ChunkPos pos) {
        this.pipeline = pipeline;
        this.position = pos;
    }

    public static ChunkGenerateTask createTask(ChunkGeneratorPipeline pipeline, ChunkPos pos) {
        return new ChunkGenerateTask(pipeline, pos);
    }

    public static Runnable createTask(ChunkGeneratorPipeline pipeline, ChunkPos pos, ArrayDeque<PrimerChunk> dest) {
        return () -> {
            ChunkGenerateTask t = createTask(pipeline, pos);
            dest.add(t.generateChunk());
        };
    }

    public ChunkPos getPosition() {
        return position;
    }

    public PrimerChunk generateChunk() {
        return this.pipeline.run(this.position);
    }

    public void completeChunk() {
        //this.pipeline.run(this.position);
    }
}
