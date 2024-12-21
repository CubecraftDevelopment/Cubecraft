package net.cubecraft.client.render.chunk.compile;

import me.gb2022.quantum3d.render.vertex.VertexBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;

public final class ChunkCompileResult {
    public static final Logger LOGGER = LogManager.getLogger("ChunkCompileResult");

    private final int x;
    private final int y;
    private final int z;
    private final VertexBuilder[][] builders;
    private final int[] layers;
    private final boolean success;

    private ChunkCompileResult(int x, int y, int z, boolean success, int[] layers) {
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

    public static ChunkCompileResult success(int x, int y, int z, int[] layers) {
        return new ChunkCompileResult(x, y, z, true, layers);
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
        return this.builders[localIndex] != null;
    }

    public int[] getLayers() {
        return layers;
    }

    public VertexBuilder[] getBuilders(int i) {
        return this.builders[i];
    }


    public boolean failed() {
        return !this.success;
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


    public void free() {
        for (var i = 0; i < this.builders.length; i++) {
            if (this.builders[i] == null) {
                continue;
            }

            for (var j = 0; j < this.builders[i].length; j++) {
                var builder = this.builders[i][j];
                if (builder == null || !builder.getLifetimeCounter().isAllocated()) {
                    continue;
                }

                builder.free();
            }

            this.builders[i] = null;
        }
    }

    @Override
    protected void finalize() {
        for (VertexBuilder[] builder : this.builders) {
            if (builder != null) {
                //todo:caching
                LOGGER.warn("un-expect free compilation: {},{},{} -> {}", x, y, z, Arrays.toString(this.layers));
                free();
                return;
            }
        }
    }
}
