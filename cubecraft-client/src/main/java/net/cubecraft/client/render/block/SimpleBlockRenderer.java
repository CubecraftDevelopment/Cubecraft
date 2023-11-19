package net.cubecraft.client.render.block;

import net.cubecraft.client.ClientRenderContext;
import net.cubecraft.client.resource.TextureAsset;
import net.cubecraft.world.IWorld;
import net.cubecraft.world.block.access.IBlockAccess;
import ink.flybird.quantum3d_legacy.draw.VertexBuilder;
import ink.flybird.quantum3d_legacy.textures.Texture2DTileMap;

import java.util.Set;

public final class SimpleBlockRenderer implements IBlockRenderer {
    private final TextureAsset texture;
    private final String layer;

    public SimpleBlockRenderer(TextureAsset texture, String layer) {
        this.texture = texture;
        this.layer = layer;
    }

    @Override
    public void renderBlock(IBlockAccess blockAccess, String layer, IWorld world, double renderX, double renderY, double renderZ, VertexBuilder builder) {
        if (this.layer != layer) {
            return;
        }

        Texture2DTileMap terrainTexture = ClientRenderContext.TEXTURE.getTexture2DTileMapContainer().get("cubecraft:terrain");
        String tid = this.texture.getAbsolutePath();
        float u0 = terrainTexture.exactTextureU(tid, 0);
        float u1 = terrainTexture.exactTextureU(tid, 1);
        float v0 = terrainTexture.exactTextureV(tid, 0);
        float v1 = terrainTexture.exactTextureV(tid, 1);
    }

    @Override
    public void initializeRenderer(Set<TextureAsset> textureList) {
        textureList.add(this.texture);
    }
}
