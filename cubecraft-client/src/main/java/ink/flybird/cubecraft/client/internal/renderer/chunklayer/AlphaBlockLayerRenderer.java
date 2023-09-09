package ink.flybird.cubecraft.client.internal.renderer.chunklayer;

import ink.flybird.cubecraft.client.internal.renderer.world.chunk.ChunkLayerRenderer;
import ink.flybird.fcommon.registry.TypeItem;
import ink.flybird.cubecraft.client.render.IRenderType;
import ink.flybird.cubecraft.client.render.RenderType;

@TypeItem("cubecraft:alpha_block")
public final class AlphaBlockLayerRenderer extends ChunkLayerRenderer {
    public AlphaBlockLayerRenderer(boolean vbo) {
        super(vbo);
    }

    @Override
    public IRenderType getRenderType() {
        return RenderType.ALPHA;
    }
}
