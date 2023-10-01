package ink.flybird.cubecraft.client.render.chunk.compile;

import ink.flybird.cubecraft.client.render.chunk.RenderChunkPos;
import ink.flybird.cubecraft.client.render.chunk.layer.ChunkLayer;
import ink.flybird.quantum3d_legacy.draw.VertexBuilder;
import ink.flybird.quantum3d_legacy.drawcall.IRenderCall;

public final class ChunkCompileResult {
    private final RenderChunkPos pos;
    private final ChunkLayer layer;
    private final VertexBuilder builder;
    private final String layerId;

    private ChunkCompileResult(RenderChunkPos pos, ChunkLayer layer, VertexBuilder builder,String layerID) {
        this.pos = pos;
        this.layer = layer;
        this.builder = builder;
        this.layerId=layerID;
    }

    public static ChunkCompileResult failed(RenderChunkPos pos,String layer) {
        return new ChunkCompileResult(pos, null, null,layer);
    }

    public static ChunkCompileResult success(RenderChunkPos pos, ChunkLayer layer, VertexBuilder builder) {
        return new ChunkCompileResult(pos, layer, builder, layer.getID());
    }

    public ChunkLayer getLayer() {
        return this.layer;
    }

    public RenderChunkPos getPos() {
        return this.pos;
    }

    public VertexBuilder getBuilder() {
        return this.builder;
    }

    public boolean isSuccess() {
        return this.layer != null;
    }

    public String getLayerId() {
        return this.layerId;
    }

    public void upload() {
        IRenderCall renderCall=this.layer.getRenderCall();
        if(!renderCall.isAllocated()){
            renderCall.allocate();
        }
        renderCall.upload(this.getBuilder());
        this.builder.free();
    }
}
