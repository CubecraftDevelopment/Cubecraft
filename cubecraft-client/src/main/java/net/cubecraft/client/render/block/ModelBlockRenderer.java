package net.cubecraft.client.render.block;

import me.gb2022.commons.registry.TypeItem;
import me.gb2022.quantum3d.render.vertex.VertexBuilder;
import net.cubecraft.client.ClientSharedContext;
import net.cubecraft.client.ClientRenderContext;
import net.cubecraft.client.render.chunk.container.ChunkLayerContainerFactory;
import net.cubecraft.client.render.model.block.BlockModel;
import net.cubecraft.client.resource.ModelAsset;
import net.cubecraft.client.resource.TextureAsset;
import net.cubecraft.resource.MultiAssetContainer;
import net.cubecraft.resource.item.IResource;
import net.cubecraft.util.register.Registered;
import net.cubecraft.world.BlockAccessor;
import net.cubecraft.world.block.access.BlockAccess;

import java.util.Collection;

@TypeItem("cubecraft:model_block")
public final class ModelBlockRenderer implements IBlockRenderer {
    public static final String LOAD_STAGE = "cubecraft:block_model";
    private final ModelAsset model;
    private BlockModel blockModel;

    public ModelBlockRenderer(ModelAsset asset) {
        this.model = asset;
        ClientSharedContext.RESOURCE_MANAGER.registerResource(
                LOAD_STAGE,
                "cubecraft:block_model@" + this.model.getAbsolutePath(),
                this.model
        );
        ClientSharedContext.RESOURCE_MANAGER.loadResource(this.model);
    }

    public ModelAsset getModel() {
        return this.model;
    }

    @Override
    public void render(BlockAccess block, BlockAccessor accessor, Registered<ChunkLayerContainerFactory.Provider> layer, int face, float x, float y, float z, VertexBuilder builder) {
        this.blockModel.render(block, accessor, layer, face, x, y, z, builder);
    }

    @Override
    public void provideTileMapItems(MultiAssetContainer<TextureAsset> list) {
        ClientRenderContext.BLOCK_MODEL.load(this.model);
        this.blockModel = ClientRenderContext.BLOCK_MODEL.get(this.model.getAbsolutePath());
        this.blockModel.provideTileMapItems(list);
    }

    @Override
    public void getResources(Collection<IResource> list) {
        list.add(this.model);
    }
}
