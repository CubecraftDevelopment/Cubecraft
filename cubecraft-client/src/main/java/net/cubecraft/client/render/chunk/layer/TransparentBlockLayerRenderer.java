package net.cubecraft.client.render.chunk.layer;

import net.cubecraft.client.render.chunk.RenderChunkPos;
import ink.flybird.fcommon.registry.TypeItem;
import net.cubecraft.client.render.IRenderType;
import net.cubecraft.client.render.RenderType;
import org.lwjgl.opengl.GL11;

@TypeItem("cubecraft:transparent_block")
public final class TransparentBlockLayerRenderer extends ChunkLayer {
    public TransparentBlockLayerRenderer(boolean vbo, RenderChunkPos pos) {
        super(vbo, pos);
    }

    @Override
    public void render() {
        GL11.glEnable(GL11.GL_CULL_FACE);
        super.render();
        GL11.glDisable(GL11.GL_CULL_FACE);
    }

    @Override
    public IRenderType getRenderType(){
        return RenderType.TRANSPARENT;
    }
}
