package net.cubecraft.client.render.chunk.compile;

import net.cubecraft.client.context.ClientRenderContext;
import net.cubecraft.client.render.chunk.RenderChunkPos;
import net.cubecraft.world.World;

public final class ChunkCompileRequest {
    private final RenderChunkPos pos;
    private final World world;

    private final ChunkLayerCompilation[] compilations;


    private ChunkCompileRequest(World world, RenderChunkPos pos, ChunkLayerCompilation... compilations) {
        this.compilations = compilations;
        this.pos = pos;
        this.world = world;
    }

    public static ChunkCompileRequest build(World world, RenderChunkPos pos) {
        return build(world, pos, ClientRenderContext.CHUNK_LAYER_RENDERER.keySet().toArray(new String[0]));
    }

    public static ChunkCompileRequest build(World world, RenderChunkPos pos, String... layers) {
        return new ChunkCompileRequest(world, pos, ChunkLayerCompilation.ofLayers(pos, layers));
    }

    public static ChunkCompileRequest build(World world, RenderChunkPos pos, String layer) {
        return new ChunkCompileRequest(world, pos, ChunkLayerCompilation.ofLayers(pos, layer));
    }


    public ChunkLayerCompilation[] getCompilations() {
        return compilations;
    }

    public RenderChunkPos getPos() {
        return this.pos;
    }

    public World getWorld() {
        return this.world;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof ChunkCompileRequest req)) {
            return false;
        }
        return req.getPos().equals(this.getPos()) && req.getWorld().equals(this.getWorld());
    }
}
