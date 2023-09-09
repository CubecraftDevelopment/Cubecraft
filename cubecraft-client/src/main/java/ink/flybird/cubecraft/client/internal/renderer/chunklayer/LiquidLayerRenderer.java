package ink.flybird.cubecraft.client.internal.renderer.chunklayer;

import ink.flybird.cubecraft.client.internal.renderer.world.chunk.ChunkLayerRenderer;
import ink.flybird.cubecraft.client.internal.renderer.world.chunk.RenderChunk;
import ink.flybird.fcommon.registry.TypeItem;
import ink.flybird.cubecraft.client.render.IRenderType;
import ink.flybird.cubecraft.client.render.RenderType;
import org.lwjgl.opengl.GL11;

@TypeItem("cubecraft:liquid")
public final class LiquidLayerRenderer extends ChunkLayerRenderer {
    public LiquidLayerRenderer(boolean vbo) {
        super(vbo);
    }

    @Override
    public void render(IRenderType type, RenderChunk chunk) {
        GL11.glDisable(GL11.GL_CULL_FACE);
        super.render(type, chunk);
        GL11.glEnable(GL11.GL_CULL_FACE);
    }

    @Override
    public IRenderType getRenderType() {
        return RenderType.TRANSPARENT;
    }
}
