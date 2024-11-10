package net.cubecraft.client.registry;

import me.gb2022.commons.registry.ConstructingMap;
import me.gb2022.commons.registry.ItemRegisterFunc;
import net.cubecraft.client.render.block.BlockRenderer;
import net.cubecraft.client.render.block.IBlockRenderer;
import net.cubecraft.client.render.block.LiquidRenderer;
import net.cubecraft.client.render.block.ModelBlockRenderer;
import net.cubecraft.client.render.chunk.TerrainRenderer;
import net.cubecraft.client.render.world.*;

public class RenderRegistry {
    @ItemRegisterFunc(IWorldRenderer.class)
    public void registerWorldRenderer(ConstructingMap<IWorldRenderer> renderers) {
        renderers.registerItem(TerrainRenderer.class);
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
}
