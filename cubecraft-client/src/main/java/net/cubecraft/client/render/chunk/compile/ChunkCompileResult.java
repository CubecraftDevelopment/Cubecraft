package net.cubecraft.client.render.chunk.compile;

import me.gb2022.quantum3d.render.vertex.VertexBuilder;
import net.cubecraft.client.render.chunk.RenderChunkPos;

public final class ChunkCompileResult {
    private final int x;
    private final int y;
    private final int z;
    private final VertexBuilder[][] builders;
    private final int[] layers;
    private final boolean success;

    private ChunkCompileResult(RenderChunkPos pos, int[] layers, boolean success) {
        this.x = pos.getX();
        this.y = pos.getY();
        this.z = pos.getZ();

        this.layers = layers;
        this.builders = new VertexBuilder[7][layers.length];
        this.success = success;
    }

    ChunkCompileResult(int x, int y, int z, boolean success, int[] layers) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.success = success;
        this.layers = layers;
        this.builders = new VertexBuilder[7][layers.length];
    }

    public static ChunkCompileResult failed(int x, int y, int z, int[] layers) {
        return new ChunkCompileResult(x, y, z, false, layers);
    }


    public void setLayerFailed(int localIndex) {
        if (!this.success) {
            throw new UnsupportedOperationException("mark-as-failed layer!");
        }
        this.builders[localIndex] = null;
    }

    public void setLayerComplete(int localIndex, VertexBuilder[] builders) {
        if (!this.success) {
            throw new UnsupportedOperationException("mark-as-failed layer!");
        }
        this.builders[localIndex] = builders;
    }

    public boolean isLayerComplete(int localIndex) {
        if (!this.success) {
            return false;
        }
        return this.builders[localIndex] != null;
    }

    public RenderChunkPos getPos() {
        return RenderChunkPos.create(x, y, z);
    }

    public int[] getLayers() {
        return layers;
    }

    public VertexBuilder[] getBuilders(int i) {
        return this.builders[i];
    }


    public boolean success() {
        return this.success;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public void freeLayer(int n) {
        if (!this.success) {
            return;
        }
        if (this.builders[n] == null) {
            return;
        }
        for (var b : this.builders[n]) {
            b.free();
        }
        this.builders[n] = null;
    }
}
