package ink.flybird.cubecraft.client.render.chunk.compile;

import ink.flybird.cubecraft.client.render.chunk.RenderChunkPos;
import ink.flybird.cubecraft.client.render.chunk.layer.ChunkLayer;
import ink.flybird.cubecraft.world.IWorld;

public final class ChunkCompileRequest {
    private final RenderChunkPos pos;
    private final ChunkLayer layer;
    private final IWorld world;
    private final String layerId;

    private ChunkCompileRequest(IWorld world, RenderChunkPos pos, String layerId, ChunkLayer layer) {
        this.pos = pos;
        this.layerId = layerId;
        this.layer = layer;
        this.world = world;
    }

    public static ChunkCompileRequest buildAt(IWorld world, RenderChunkPos pos, String layerId) {
        return new ChunkCompileRequest(world, pos, layerId, null);
    }

    public static ChunkCompileRequest rebuildAt(IWorld world, RenderChunkPos pos, ChunkLayer layer) {
        return new ChunkCompileRequest(world, pos, layer.getID(), layer);
    }

    public ChunkLayer getLayer() {
        return this.layer;
    }

    public RenderChunkPos getPos() {
        return this.pos;
    }

    public IWorld getWorld() {
        return this.world;
    }

    public String getLayerId() {
        return this.layerId;
    }
}
