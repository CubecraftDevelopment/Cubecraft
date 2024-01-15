package net.cubecraft.client.render.block;

import ink.flybird.fcommon.event.EventHandler;
import ink.flybird.quantum3d_legacy.draw.VertexBuilder;
import net.cubecraft.client.ClientSharedContext;
import net.cubecraft.client.context.ClientRenderContext;
import net.cubecraft.client.render.model.block.BlockModel;
import net.cubecraft.client.resource.ModelAsset;
import net.cubecraft.client.resource.TextureAsset;
import net.cubecraft.world.IWorld;
import net.cubecraft.world.block.access.IBlockAccess;

import java.util.Set;

public final class ModelBlockRenderer implements IBlockRenderer {
    public static final String BLOCK_MODEL_LOAD_STAGE = "cubecraft:block_model";
    private final ModelAsset model;
    private BlockModel blockModel;

    public ModelBlockRenderer(ModelAsset asset) {
        this.model = asset;
        ClientSharedContext.RESOURCE_MANAGER.registerResource(BLOCK_MODEL_LOAD_STAGE, "cubecraft:block_model@" + this.model.getAbsolutePath(), this.model);
        ClientSharedContext.RESOURCE_MANAGER.loadResource(this.model);
    }

    public ModelAsset getModel() {
        return this.model;
    }

    @Override
    public void renderBlock(IBlockAccess blockAccess, String layer, IWorld world, double renderX, double renderY, double renderZ, VertexBuilder builder) {
        if (this.blockModel == null) {
            this.blockModel = net.cubecraft.client.context.ClientRenderContext.BLOCK_MODEL.get(this.model.getAbsolutePath());
        }
        this.blockModel.render(blockAccess, builder, layer, world, renderX, renderY, renderZ);
    }

    @Override
    public void initializeRenderer(Set<TextureAsset> textureList) {

        net.cubecraft.client.context.ClientRenderContext.BLOCK_MODEL.load(this.model);
        ClientRenderContext.BLOCK_MODEL.get(this.model.getAbsolutePath()).initializeModel(textureList);
    }

    @EventHandler
    public void onResourceReloadFinish() {

    }
}
