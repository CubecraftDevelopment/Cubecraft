package net.cubecraft.client.render.model.block;

import me.gb2022.quantum3d.render.vertex.VertexBuilder;
import net.cubecraft.client.render.chunk.container.ChunkLayerContainerFactory;
import net.cubecraft.client.render.chunk.container.ChunkLayerContainers;
import net.cubecraft.client.render.model.ColorMap;
import net.cubecraft.client.resource.TextureAsset;
import net.cubecraft.resource.MultiAssetContainer;
import net.cubecraft.util.register.Registered;
import net.cubecraft.world.BlockAccessor;
import net.cubecraft.world.block.access.BlockAccess;

import java.util.Set;

public final class CrossBlockModel extends BlockModel {
    private final TextureAsset texture;
    private final ColorMap color;
    private final int localizedLayer;
    private final String layer;

    public CrossBlockModel(TextureAsset texture, ColorMap color, String layer) {
        this.texture = texture;
        this.color = color;
        this.localizedLayer = ChunkLayerContainers.REGISTRY.id(layer);
        this.layer = layer;
    }

    @Override
    public String getParticleTexture() {
        return this.texture.getAbsolutePath();
    }

    @Override
    public void render(BlockAccess block, BlockAccessor accessor, Registered<ChunkLayerContainerFactory.Provider> layer, int face, float x, float y, float z, VertexBuilder builder) {

    }

    @Override
    public void provideTileMapItems(MultiAssetContainer<TextureAsset> list) {
        list.addResource(this.layer, this.texture);
    }
}
