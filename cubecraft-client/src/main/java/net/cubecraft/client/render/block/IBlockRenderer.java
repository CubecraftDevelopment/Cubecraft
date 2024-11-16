package net.cubecraft.client.render.block;

import me.gb2022.quantum3d.render.vertex.VertexBuilder;
import net.cubecraft.client.render.chunk.container.ChunkLayerContainerFactory;
import net.cubecraft.client.render.model.CullingPredication;
import net.cubecraft.client.resource.TextureAsset;
import net.cubecraft.resource.MultiAssetContainer;
import net.cubecraft.resource.ResourceHolder;
import net.cubecraft.resource.item.IResource;
import net.cubecraft.util.register.Registered;
import net.cubecraft.world.BlockAccessor;
import net.cubecraft.world.block.access.BlockAccess;

import java.util.Collection;

public interface IBlockRenderer extends ResourceHolder {
    static boolean isFaceCulled(CullingPredication culling, int face, BlockAccess block, BlockAccessor accessor) {
        if (block == null || accessor == null) {
            return false;
        }

        return culling.test(block, block.near(accessor, face, 1));
    }

    default void provideTileMapItems(MultiAssetContainer<TextureAsset> list) {
    }

    void render(BlockAccess block, BlockAccessor accessor, Registered<ChunkLayerContainerFactory.Provider> layer, int face, float x, float y, float z, VertexBuilder builder);

    @Override
    default void getResources(Collection<IResource> list) {
    }
}
