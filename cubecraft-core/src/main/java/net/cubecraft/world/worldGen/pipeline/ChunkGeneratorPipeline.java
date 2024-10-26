package net.cubecraft.world.worldGen.pipeline;

import net.cubecraft.world.chunk.*;
import net.cubecraft.world.chunk.pos.ChunkPos;
import net.cubecraft.world.worldGen.WorldGenerator;
import net.cubecraft.world.worldGen.structure.StructureController;

import java.util.HashSet;
import java.util.Set;

public final class ChunkGeneratorPipeline {
    private final ChunkCache<Chunk> generatorCache = new ChunkCache<>();
    private final Set<ChunkPos> fullGenerated = new HashSet<>();

    private final PipelineStorage beforeStructure = new PipelineStorage(this);
    private final PipelineStorage afterStructure = new PipelineStorage(this);

    private final Set<StructureController> structureControllers = new HashSet<>();

    private final PipelineContext context;

    public ChunkGeneratorPipeline(long seed) {
        this.context = new PipelineContext(seed);
    }

    public PipelineStorage beforeStructure() {
        return beforeStructure;
    }

    public PipelineStorage afterStructure() {
        return afterStructure;
    }

    public ChunkGeneratorPipeline addStructure(StructureController controller) {
        structureControllers.add(controller);
        return this;
    }

    private void genChunkBeforeStructure(ChunkPos p) {
        PrimerChunk chunk = new PrimerChunk(p);

        for (TerrainGeneratorHandler handler : this.beforeStructure.getHandlerList()) {
            handler.generate(chunk, this.context, chunk.getContext());
        }

        chunk.calcHeightMap();

        this.generatorCache.add(chunk);
    }

    public PrimerChunk run(ChunkPos pos) {
        PrimerChunk chunk = new PrimerChunk(pos);

        for (TerrainGeneratorHandler handler : this.beforeStructure.getHandlerList()) {
            handler.generate(chunk, this.context, chunk.getContext());
        }

        chunk.calcHeightMap();

        return chunk;
    }

    public void completeChunk(WorldChunk chunk, WorldGenerator generator) {
        var cache = chunk.getWorld().getChunkCache();
        var pos = chunk.getKey();

        chunk.calcHeightMap();

        for (ChunkPos p : pos.getNearSquared()) {
            if (cache.contains(p)) {
                continue;
            }
            generator.load(p.getX(),p.getZ(),ChunkState.STRUCTURE);
        }

        for (StructureController controller : this.structureControllers) {
            controller.generateChunk(chunk, cache);
        }
    }
}
