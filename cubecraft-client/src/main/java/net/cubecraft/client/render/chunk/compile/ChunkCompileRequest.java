package net.cubecraft.client.render.chunk.compile;

import net.cubecraft.client.render.chunk.RenderChunkPos;
import net.cubecraft.client.render.chunk.layer.ChunkLayer;
import net.cubecraft.world.IWorld;

import java.util.Objects;

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

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof ChunkCompileRequest req)) {
            return false;
        }
        return req.getPos().equals(this.getPos()) && Objects.equals(req.getLayerId(), this.layerId);
    }
}
