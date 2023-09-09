package ink.flybird.cubecraft.client.internal.renderer.world.chunk;

import ink.flybird.fcommon.registry.TypeItem;
import ink.flybird.cubecraft.client.render.IRenderType;
import ink.flybird.quantum3d.drawcall.IRenderCall;


public abstract class ChunkLayerRenderer {
    private final IRenderCall renderCall;
    private boolean filled = false;

    public ChunkLayerRenderer(boolean vbo) {
        this.renderCall = IRenderCall.create(vbo);
    }

    public void allocate() {
        this.renderCall.allocate();
    }

    public void destroy() {
        this.renderCall.free();
    }

    public void render(IRenderType type, RenderChunk chunk) {
        if (this.getRenderType() != type) {
            return;
        }
        if (!this.shouldRender(chunk)) {
            return;
        }
        if (!this.filled) {
            return;
        }
        this.renderCall.call();
    }

    public boolean isFilled() {
        return filled;
    }

    public void setFilled(boolean filled) {
        this.filled = filled;
    }

    public IRenderCall getRenderCall() {
        return this.renderCall;
    }

    public boolean shouldRender(RenderChunk chunk) {
        return true;
    }

    public abstract IRenderType getRenderType();

    public String getID() {
        return this.getClass().getAnnotation(TypeItem.class).value();
    }
}
