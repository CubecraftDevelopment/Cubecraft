package net.cubecraft.client.registry;

import net.cubecraft.client.render.block.BlockRenderer;
import net.cubecraft.client.render.block.LiquidRenderer;
import net.cubecraft.client.render.block.ModelBlockRenderer;
import net.cubecraft.client.render.chunk.layer.AlphaBlockLayerRenderer;
import net.cubecraft.client.render.chunk.layer.TransparentBlockLayerRenderer;
import net.cubecraft.client.render.chunk.layer.ChunkLayer;
import net.cubecraft.client.render.block.IBlockRenderer;

import ink.flybird.fcommon.registry.ItemRegisterFunc;
import ink.flybird.fcommon.registry.ConstructingMap;

public class RenderRegistry {
    @ItemRegisterFunc(IBlockRenderer.class)
    public void registerBlockRenderer(ConstructingMap<IBlockRenderer> renderers) {
        renderers.registerItem(LiquidRenderer.class);
        renderers.registerItem(BlockRenderer.class);
        renderers.registerItem(ModelBlockRenderer.class);
    }

    @ItemRegisterFunc(ChunkLayer.class)
    public void registerRenderChunkContainer(ConstructingMap<ChunkLayer> renderers) {
        renderers.registerItem(AlphaBlockLayerRenderer.class);
        renderers.registerItem(TransparentBlockLayerRenderer.class);
    }
}
