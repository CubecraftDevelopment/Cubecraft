package net.cubecraft.client.registry;

import net.cubecraft.client.render.block.BlockRenderer;
import net.cubecraft.client.render.block.LiquidRenderer;
import net.cubecraft.client.render.block.ModelBlockRenderer;
import net.cubecraft.client.render.chunk.ChunkRenderer;
import net.cubecraft.client.render.chunk.layer.AlphaBlockLayerRenderer;
import net.cubecraft.client.render.chunk.layer.TransparentBlockLayerRenderer;
import net.cubecraft.client.render.chunk.layer.ChunkLayer;
import net.cubecraft.client.render.block.IBlockRenderer;

import me.gb2022.commons.registry.ItemRegisterFunc;
import me.gb2022.commons.registry.ConstructingMap;
import net.cubecraft.client.render.world.*;

public class RenderRegistry {
    @ItemRegisterFunc(IWorldRenderer.class)
    public void registerWorldRenderer(ConstructingMap<IWorldRenderer> renderers) {
        renderers.registerItem(ChunkRenderer.class);
        renderers.registerItem(CloudRenderer.class);
        renderers.registerItem(EntityRenderer.class);
        renderers.registerItem(HUDRenderer.class);
        renderers.registerItem(SkyBoxRenderer.class);
        renderers.registerItem(ParticleRenderer.class);
    }

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
