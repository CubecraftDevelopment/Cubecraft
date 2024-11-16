package net.cubecraft.world.worldGen;

import net.cubecraft.world.World;
import net.cubecraft.world.chunk.ChunkState;
import net.cubecraft.world.chunk.PrimerChunk;
import net.cubecraft.world.chunk.WorldChunk;
import net.cubecraft.world.chunk.pos.ChunkPos;
import net.cubecraft.world.storage.PersistentChunkHolder;
import net.cubecraft.world.worldGen.pipeline.ChunkGenerateTask;
import net.cubecraft.world.worldGen.pipeline.ChunkGeneratorPipeline;
import net.cubecraft.world.worldGen.pipeline.pipelines.OverworldPipeline;

import java.util.HashMap;

public class WorldGenerator {
    private final World world;
    private final HashMap<String, ChunkGeneratorPipeline> pipelineCache = new HashMap<>();
    private PersistentChunkHolder persistentChunkHolder;

    public WorldGenerator(World world) {
        this.world = world;

        long seed = world.getLevel().getLevelInfo().getSeed();
        this.pipelineCache.put(world.getId(), new OverworldPipeline().build(world.getId(), seed));
    }

    public static long hash(int x, int z, ChunkState state) {
        return ChunkPos.encode(x, z) & (state.ordinal() << 3) | state.ordinal() & 0xFA;
    }

    public PrimerChunk generate(World world, int x, int z) {
        var pos = new ChunkPos(x, z);
        var pipeline = this.pipelineCache.get(world.getId());
        return ChunkGenerateTask.createTask(pipeline, pos).generateChunk();
    }

    public void structure(WorldChunk chunk) {
        chunk.setState(ChunkState.STRUCTURE);
    }

    public void feature(WorldChunk chunk) {
        var pipeline = this.pipelineCache.get(chunk.getWorld().getId());
        pipeline.completeChunk(chunk, this);
        chunk.setState(ChunkState.COMPLETE);
    }

    public WorldChunk load(int x, int z, ChunkState state) {
        if (state == ChunkState.EMPTY) {
            throw new IllegalArgumentException("what should i do to create an empty chunk");
        }

        var chunk = this.world.getChunkCache().cachedGet(x, z);

        if (chunk == null && this.persistentChunkHolder != null) {
            chunk = this.persistentChunkHolder.load(this.world, x, z);
            if (chunk != null) {
                chunk.calcHeightMap();
            }
        }

        if (chunk == null) {
            chunk = new WorldChunk(this.world, generate(this.world, x, z));
            this.world.getChunkCache().add(chunk);
        }

        if (chunk.getState() == ChunkState.COMPLETE) {
            return chunk;
        }

        if (state == ChunkState.TERRAIN) {
            return chunk;
        }

        if (!chunk.getState().isComplete(ChunkState.STRUCTURE)) {
            this.structure(chunk);
            chunk.calcHeightMap();
        }

        if (state == ChunkState.STRUCTURE) {
            return chunk;
        }

        if (!chunk.getState().isComplete(ChunkState.COMPLETE)) {
            this.feature(chunk);
            chunk.calcHeightMap();
        }

        chunk.setState(ChunkState.COMPLETE);
        return chunk;
    }

    public void setPersistentChunkProvider(PersistentChunkHolder provider) {
        this.persistentChunkHolder = provider;
    }

    public void unload(WorldChunk chunk) {
        if (this.persistentChunkHolder == null) {
            return;
        }
        this.persistentChunkHolder.save(chunk);
    }


    public World getWorld() {
        return world;
    }
}
