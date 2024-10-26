package net.cubecraft.client.render.block;

import ink.flybird.quantum3d_legacy.draw.VertexBuilder;
import me.gb2022.commons.event.EventHandler;
import me.gb2022.commons.registry.TypeItem;
import net.cubecraft.client.ClientSharedContext;
import net.cubecraft.client.context.ClientRenderContext;
import net.cubecraft.client.render.model.block.BlockModel;
import net.cubecraft.client.resource.ModelAsset;
import net.cubecraft.client.resource.TextureAsset;
import net.cubecraft.world.BlockAccessor;
import net.cubecraft.world.World;
import net.cubecraft.world.block.access.IBlockAccess;

import java.util.Set;

@TypeItem("cubecraft:model_block")
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
    public void renderBlock(IBlockAccess blockAccess, String layer, World world, double renderX, double renderY, double renderZ, VertexBuilder builder) {

    }

    @Override
    public void renderBlock(IBlockAccess block, String layer, BlockAccessor world, int face, double renderX, double renderY, double renderZ, VertexBuilder builder) {
        if (this.blockModel == null) {
            this.blockModel = ClientRenderContext.BLOCK_MODEL.get(this.model.getAbsolutePath());
        }
        this.blockModel.renderBlock(block, layer,world, face, renderX, renderY, renderZ, builder);
    }

    @Override
    public void initializeRenderer(Set<TextureAsset> textureList) {
        ClientRenderContext.BLOCK_MODEL.load(this.model);
        ClientRenderContext.BLOCK_MODEL.get(this.model.getAbsolutePath()).initializeModel(textureList);
    }

    @EventHandler
    public void onResourceReloadFinish() {

    }
}
