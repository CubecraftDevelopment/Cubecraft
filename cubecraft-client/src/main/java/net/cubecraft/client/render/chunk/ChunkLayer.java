package net.cubecraft.client.render.chunk;

import me.gb2022.commons.registry.TypeItem;
import me.gb2022.quantum3d.render.vertex.VertexBuilder;
import net.cubecraft.client.render.chunk.compile.ModernChunkCompiler;
import net.cubecraft.client.render.chunk.status.RenderChunkStatus;

import java.util.function.Consumer;

/**
 * represents a set of render commands, owned by a specific {RenderChunkStatus}
 *
 * @see RenderChunkStatus
 */
public final class ChunkLayer {
    private final RenderChunkStatus owner;
    private final RenderBatch[] batches = new RenderBatch[7];
    private boolean filled = false;
    private boolean active = false;

    public ChunkLayer(RenderChunkStatus owner, boolean vbo) {
        this.owner = owner;

        for (int i = 0; i < 7; i++) {
            this.batches[i] = RenderBatch.create(vbo);
        }
    }

    public static long hash(int x, int y, int z) {
        return x ^ ((long) y << 8) ^ ((long) z << 16);
    }

    public void allocate() {
        for (int i = 0; i < 7; i++) {
            this.batches[i].allocate();
        }
    }

    public void destroy() {
        for (int i = 0; i < 7; i++) {
            this.batches[i].free();
        }
    }

    public void render() {
        render(RenderBatch::call);
    }

    public void render(Consumer<RenderBatch> consumer) {
        if (!this.filled) {
            return;
        }

        consumer.accept(this.batches[6]);
        for (int i = 0; i < this.batches.length - 1; i++) {
            if (!getOwner().getFaceVisibility(i)) {
                continue;
            }
            consumer.accept(this.batches[i]);
        }
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean filled() {
        return this.filled;
    }

    public void filled(boolean filled) {
        this.filled = filled;
    }

    public String id() {
        return this.getClass().getAnnotation(TypeItem.class).value();
    }

    public RenderBatch[] batches() {
        return this.batches;
    }

    public RenderChunkStatus getOwner() {
        return owner;
    }

    public void upload(VertexBuilder[] builder) {
        this.filled = true;

        for (int i = 0; i < this.batches.length; i++) {
            this.batches[i].upload(builder[i]);
        }
    }
}
