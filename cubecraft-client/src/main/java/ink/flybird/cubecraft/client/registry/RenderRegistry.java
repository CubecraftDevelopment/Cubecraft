package ink.flybird.cubecraft.client.registry;

import ink.flybird.cubecraft.client.render.block.BlockRenderer;
import ink.flybird.cubecraft.client.render.block.LiquidRenderer;
import ink.flybird.cubecraft.client.render.block.ModelRenderer;
import ink.flybird.cubecraft.client.render.chunk.ChunkRenderer;
import ink.flybird.cubecraft.client.render.world.*;
import ink.flybird.cubecraft.client.render.chunk.layer.AlphaBlockLayerRenderer;
import ink.flybird.cubecraft.client.render.chunk.layer.TransparentBlockLayerRenderer;
import ink.flybird.cubecraft.client.render.chunk.layer.ChunkLayer;
import ink.flybird.cubecraft.client.render.block.IBlockRenderer;
import ink.flybird.cubecraft.client.render.world.IWorldRenderer;

import ink.flybird.fcommon.registry.ItemRegisterFunc;
import ink.flybird.fcommon.registry.ConstructingMap;

public class RenderRegistry {
    @ItemRegisterFunc(IWorldRenderer.class)
    public void registerWorldRenderer(ConstructingMap<IWorldRenderer> renderers) {
        renderers.registerItem(ChunkRenderer.class);
        renderers.registerItem(EntityRenderer.class);
        renderers.registerItem(HUDRenderer.class);
        renderers.registerItem(ParticleRenderer.class);
        renderers.registerItem(SkyBoxRenderer.class);
        renderers.registerItem(CloudRenderer.class);
    }

    @ItemRegisterFunc(IBlockRenderer.class)
    public void registerBlockRenderer(ConstructingMap<IBlockRenderer> renderers) {
        renderers.registerItem(LiquidRenderer.class);
        renderers.registerItem(BlockRenderer.class);
        renderers.registerItem(ModelRenderer.class);
    }

    @ItemRegisterFunc(ChunkLayer.class)
    public void registerRenderChunkContainer(ConstructingMap<ChunkLayer> renderers) {
        renderers.registerItem(AlphaBlockLayerRenderer.class);
        renderers.registerItem(TransparentBlockLayerRenderer.class);
    }
}
