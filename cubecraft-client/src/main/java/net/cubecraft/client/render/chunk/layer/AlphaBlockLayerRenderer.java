package net.cubecraft.client.render.chunk.layer;

import net.cubecraft.client.render.chunk.RenderChunkPos;
import me.gb2022.commons.registry.TypeItem;
import net.cubecraft.client.render.IRenderType;
import net.cubecraft.client.render.RenderType;
import org.lwjgl.opengl.GL11;

@TypeItem("cubecraft:alpha_block")
public final class AlphaBlockLayerRenderer extends ChunkLayer {
    public AlphaBlockLayerRenderer(boolean vbo, RenderChunkPos pos) {
        super(vbo, pos);
    }

    @Override
    public void render() {
        GL11.glEnable(GL11.GL_CULL_FACE);
        super.render();
    }

    @Override
    public IRenderType getRenderType() {
        return RenderType.ALPHA;
    }
}
