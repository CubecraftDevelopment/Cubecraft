package ink.flybird.cubecraft.client.render.chunk.layer;

import ink.flybird.cubecraft.client.render.IRenderType;
import ink.flybird.cubecraft.client.render.chunk.RenderChunkPos;
import ink.flybird.fcommon.registry.TypeItem;
import ink.flybird.quantum3d_legacy.drawcall.IRenderCall;


public abstract class ChunkLayer {
    private final IRenderCall renderCall;
    private final RenderChunkPos pos;
    private boolean filled = false;

    public ChunkLayer(boolean vbo, RenderChunkPos pos) {
        this.renderCall = IRenderCall.create(vbo);
        this.pos = pos;
    }

    public static String encode(String layer, long x, long y, long z) {
        return layer + "{" + x + "," + y + "," + z + "}";
    }

    public static String encode(String layer, RenderChunkPos pos) {
        return encode(layer, pos.getX(), pos.getY(), pos.getZ());
    }

    public void allocate() {
        this.renderCall.allocate();
    }

    public void destroy() {
        this.renderCall.free();
    }

    public void render() {
        if (!this.filled) {
            return;
        }
        this.renderCall.call();
    }

    public RenderChunkPos getPos() {
        return this.pos;
    }

    public boolean isFilled() {
        return this.filled;
    }

    public void setFilled(boolean filled) {
        this.filled = filled;
    }

    public IRenderCall getRenderCall() {
        return this.renderCall;
    }

    public abstract IRenderType getRenderType();

    public String getID() {
        return this.getClass().getAnnotation(TypeItem.class).value();
    }

    public String encode() {
        return encode(this.getID(), this.getPos());
    }

    @Override
    public String toString() {
        return "%s{call=%d,code=%s}".formatted(this.getClass().getSimpleName(), this.getRenderCall().getHandle(), this.encode());
    }
}
