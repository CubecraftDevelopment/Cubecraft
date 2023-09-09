package ink.flybird.cubecraft.client.internal.registry;

import ink.flybird.cubecraft.client.internal.renderer.block.BlockRenderer;
import ink.flybird.cubecraft.client.internal.renderer.block.LiquidRenderer;
import ink.flybird.cubecraft.client.internal.renderer.block.ModelRenderer;
import ink.flybird.cubecraft.client.internal.renderer.chunklayer.AlphaBlockLayerRenderer;
import ink.flybird.cubecraft.client.internal.renderer.chunklayer.LiquidLayerRenderer;
import ink.flybird.cubecraft.client.internal.renderer.chunklayer.TransparentBlockLayerRenderer;
import ink.flybird.cubecraft.client.internal.renderer.world.*;
import ink.flybird.cubecraft.client.internal.renderer.world.chunk.ChunkLayerRenderer;
import ink.flybird.cubecraft.client.render.renderer.IBlockRenderer;
import ink.flybird.cubecraft.client.render.renderer.IWorldRenderer;

import ink.flybird.fcommon.registry.ItemRegisterFunc;
import ink.flybird.fcommon.registry.ConstructingMap;

public class RenderRegistry {
    @ItemRegisterFunc(IWorldRenderer.class)
    public void registerWorldRenderer(ConstructingMap<IWorldRenderer> renderers) {
        renderers.registerItem(TerrainRenderer.class);
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

    @ItemRegisterFunc(ChunkLayerRenderer.class)
    public void registerRenderChunkContainer(ConstructingMap<ChunkLayerRenderer> renderers) {
        renderers.registerItem(AlphaBlockLayerRenderer.class);
        renderers.registerItem(TransparentBlockLayerRenderer.class);
        renderers.registerItem(LiquidLayerRenderer.class);
    }
}
