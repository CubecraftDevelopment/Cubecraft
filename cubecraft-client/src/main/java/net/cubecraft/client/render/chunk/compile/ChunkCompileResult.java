package net.cubecraft.client.render.chunk.compile;

import ink.flybird.quantum3d_legacy.draw.VertexBuilder;
import ink.flybird.quantum3d_legacy.drawcall.IRenderCall;
import net.cubecraft.client.render.chunk.ChunkRenderer;
import net.cubecraft.client.render.chunk.RenderChunkPos;
import net.cubecraft.client.render.chunk.layer.ChunkLayer;

public final class ChunkCompileResult {
    private final RenderChunkPos pos;
    private final ChunkLayer layer;
    private final VertexBuilder[] builders;
    private final String layerId;

    private ChunkCompileResult(RenderChunkPos pos, ChunkLayer layer, VertexBuilder[] builders, String layerID) {
        this.pos = pos;
        this.layer = layer;
        this.builders = builders;
        this.layerId = layerID;
    }

    public static ChunkCompileResult failed(RenderChunkPos pos, String layer) {
        return new ChunkCompileResult(pos, null, null, layer);
    }

    public static ChunkCompileResult success(RenderChunkPos pos, ChunkLayer layer, VertexBuilder[] builder) {
        return new ChunkCompileResult(pos, layer, builder, layer.getID());
    }

    public ChunkLayer getLayer() {
        return this.layer;
    }

    public RenderChunkPos getPos() {
        return this.pos;
    }

    public VertexBuilder[] getBuilders() {
        return this.builders;
    }

    public boolean isSuccess() {
        return this.layer != null;
    }

    public String getLayerId() {
        return this.layerId;
    }

    public void destroy(){
        for (int i = 0; i < 7; i++) {
            this.builders[i].freeAsync();
        }
    }

    public void upload() {
        for (int i = 0; i < 7; i++) {
            IRenderCall call = this.layer.getBatches()[i];
            if (!call.isAllocated()) {
                call.allocate();
            }

            call.upload(this.builders[i]);
        }
        destroy();
    }
}
