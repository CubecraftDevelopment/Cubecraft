package net.cubecraft.client.render.chunk.compile;

import ink.flybird.quantum3d_legacy.draw.DrawMode;
import ink.flybird.quantum3d_legacy.draw.OffHeapVertexBuilder;
import ink.flybird.quantum3d_legacy.draw.VertexBuilder;
import ink.flybird.quantum3d_legacy.drawcall.IRenderCall;
import net.cubecraft.client.ClientSettingRegistry;
import net.cubecraft.client.context.ClientRenderContext;
import net.cubecraft.client.render.chunk.ChunkRenderer;
import net.cubecraft.client.render.chunk.RenderChunkPos;
import net.cubecraft.client.render.chunk.layer.ChunkLayer;

public final class ChunkLayerCompilation {
    private final String layerId;
    private final ChunkLayer layer;
    private final VertexBuilder[] builders = new VertexBuilder[7];


    public ChunkLayerCompilation(String layerId, ChunkLayer layer) {
        this.layerId = layerId;
        this.layer = layer;
    }


    public ChunkLayerCompilation(String layerId, RenderChunkPos pos) {
        boolean vbo = ClientSettingRegistry.CHUNK_USE_VBO.getValue();
        this.layerId = layerId;
        this.layer = ClientRenderContext.CHUNK_LAYER_RENDERER.create(layerId, vbo, pos);
    }

    public static ChunkLayerCompilation[] ofLayers(RenderChunkPos pos, String... layers) {
        ChunkLayerCompilation[] result = new ChunkLayerCompilation[layers.length];
        for (int i = 0; i < layers.length; i++) {
            result[i] = new ChunkLayerCompilation(layers[i], pos);
        }
        return result;
    }

    public void allocate() {
        for (int i = 0; i < builders.length; i++) {
            this.builders[i] = new OffHeapVertexBuilder(16384, DrawMode.QUADS);
        }
    }

    public VertexBuilder[] getBuilders() {
        return builders;
    }

    public void free() {
        for (VertexBuilder builder : builders) {
            builder.free();
        }
    }

    public ChunkLayer getLayer() {
        return layer;
    }

    public String getLayerId() {
        return layerId;
    }

    public void upload() {
        for (int i = 0; i < 7; i++) {
            IRenderCall call = this.layer.getBatches()[i];
            if (!call.isAllocated()) {
                call.allocate();
            }

            call.upload(this.builders[i]);
        }
        this.free();
    }
}
