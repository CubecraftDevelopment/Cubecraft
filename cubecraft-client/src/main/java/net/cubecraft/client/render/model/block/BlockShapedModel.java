package net.cubecraft.client.render.model.block;

import com.google.gson.JsonObject;
import me.gb2022.quantum3d.render.vertex.VertexBuilder;
import net.cubecraft.client.registry.ColorMaps;
import net.cubecraft.client.render.block.BlockBakery;
import net.cubecraft.client.render.block.IBlockRenderer;
import net.cubecraft.client.render.chunk.container.ChunkLayerContainerFactory;
import net.cubecraft.client.render.chunk.container.ChunkLayerContainers;
import net.cubecraft.client.render.model.ColorMap;
import net.cubecraft.client.render.model.CullingPredication;
import net.cubecraft.client.render.model.object.Vertex;
import net.cubecraft.client.resource.TextureAsset;
import net.cubecraft.resource.MultiAssetContainer;
import net.cubecraft.util.DeserializedConstructor;
import net.cubecraft.util.register.Registered;
import net.cubecraft.world.BlockAccessor;
import net.cubecraft.world.block.access.BlockAccess;

public abstract class BlockShapedModel extends BlockModel {
    private final String[] faceTextures = new String[6];
    private final CullingPredication culling;
    private final ColorMap color;
    private final int localizedLayer;
    private final String layer;

    public BlockShapedModel(CullingPredication culling, ColorMap color, String layer) {
        this.culling = culling;
        this.color = color;
        this.localizedLayer = ChunkLayerContainers.REGISTRY.id(layer);
        this.layer = layer;
    }

    @DeserializedConstructor
    public BlockShapedModel(JsonObject obj) {
        this(
                CullingPredication.REGISTRY.object(obj.get("culling").getAsString()),
                obj.has("color") ? ColorMaps.REGISTRY.object(obj.get("color").getAsString()) : null,
                obj.get("layer").getAsString()
        );
    }

    public abstract TextureAsset getTextureForFace(int f);

    @SuppressWarnings("DuplicatedCode")
    @Override
    public void render(BlockAccess block, BlockAccessor accessor, Registered<ChunkLayerContainerFactory.Provider> layer, int face, float x, float y, float z, VertexBuilder builder) {
        if (face == 6) {
            return;
        }
        if (layer != null && this.localizedLayer != layer.getId()) {
            return;
        }
        if (IBlockRenderer.isFaceCulled(this.culling, face, block, accessor)) {
            return;
        }

        var color = 0xFFFFFF;

        if (this.color != null) {
            color = this.color.sample(accessor, block);
        }

        var faceTexture = this.faceTextures[face];
        var terrainTexture = layer.get().getTextureUsed();

        var u0 = terrainTexture.exactTextureU(faceTexture, 0);
        var u1 = terrainTexture.exactTextureU(faceTexture, 1);
        var v0 = terrainTexture.exactTextureV(faceTexture, 0);
        var v1 = terrainTexture.exactTextureV(faceTexture, 1);

        var x0 = x + 0.0f;
        var y0 = y + 0.0f;
        var z0 = z + 0.0f;
        var x1 = x0 + 1;
        var y1 = y0 + 1;
        var z1 = z0 + 1;

        int c = color;

        switch (face) {
            case 0 -> {
                Vertex.draw(builder, x1, y1, z1, u1, v1, c, BlockBakery.bakeLight(accessor, x1, y1, z1, block, face));
                Vertex.draw(builder, x1, y1, z0, u1, v0, c, BlockBakery.bakeLight(accessor, x1, y1, z0, block, face));
                Vertex.draw(builder, x0, y1, z0, u0, v0, c, BlockBakery.bakeLight(accessor, x0, y1, z0, block, face));
                Vertex.draw(builder, x0, y1, z1, u0, v1, c, BlockBakery.bakeLight(accessor, x0, y1, z1, block, face));
            }
            case 1 -> {
                Vertex.draw(builder, x0, y0, z1, u0, v1, c, BlockBakery.bakeLight(accessor, x0, y0, z1, block, face));
                Vertex.draw(builder, x0, y0, z0, u0, v0, c, BlockBakery.bakeLight(accessor, x0, y0, z0, block, face));
                Vertex.draw(builder, x1, y0, z0, u1, v0, c, BlockBakery.bakeLight(accessor, x1, y0, z0, block, face));
                Vertex.draw(builder, x1, y0, z1, u1, v1, c, BlockBakery.bakeLight(accessor, x1, y0, z1, block, face));
            }
            case 2 -> {
                Vertex.draw(builder, x0, y1, z1, u0, v0, c, BlockBakery.bakeLight(accessor, x0, y1, z1, block, face));
                Vertex.draw(builder, x0, y0, z1, u0, v1, c, BlockBakery.bakeLight(accessor, x0, y0, z1, block, face));
                Vertex.draw(builder, x1, y0, z1, u1, v1, c, BlockBakery.bakeLight(accessor, x1, y0, z1, block, face));
                Vertex.draw(builder, x1, y1, z1, u1, v0, c, BlockBakery.bakeLight(accessor, x1, y1, z1, block, face));
            }
            case 3 -> {
                Vertex.draw(builder, x0, y1, z0, u1, v0, c, BlockBakery.bakeLight(accessor, x0, y1, z0, block, face));
                Vertex.draw(builder, x1, y1, z0, u0, v0, c, BlockBakery.bakeLight(accessor, x1, y1, z0, block, face));
                Vertex.draw(builder, x1, y0, z0, u0, v1, c, BlockBakery.bakeLight(accessor, x1, y0, z0, block, face));
                Vertex.draw(builder, x0, y0, z0, u1, v1, c, BlockBakery.bakeLight(accessor, x0, y0, z0, block, face));
            }
            case 4 -> {
                Vertex.draw(builder, x1, y0, z1, u0, v1, c, BlockBakery.bakeLight(accessor, x1, y0, z1, block, face));
                Vertex.draw(builder, x1, y0, z0, u1, v1, c, BlockBakery.bakeLight(accessor, x1, y0, z0, block, face));
                Vertex.draw(builder, x1, y1, z0, u1, v0, c, BlockBakery.bakeLight(accessor, x1, y1, z0, block, face));
                Vertex.draw(builder, x1, y1, z1, u0, v0, c, BlockBakery.bakeLight(accessor, x1, y1, z1, block, face));
            }
            case 5 -> {
                Vertex.draw(builder, x0, y1, z1, u1, v0, c, BlockBakery.bakeLight(accessor, x0, y1, z1, block, face));
                Vertex.draw(builder, x0, y1, z0, u0, v0, c, BlockBakery.bakeLight(accessor, x0, y1, z0, block, face));
                Vertex.draw(builder, x0, y0, z0, u0, v1, c, BlockBakery.bakeLight(accessor, x0, y0, z0, block, face));
                Vertex.draw(builder, x0, y0, z1, u1, v1, c, BlockBakery.bakeLight(accessor, x0, y0, z1, block, face));
            }
        }
    }


    @Override
    public void provideTileMapItems(MultiAssetContainer<TextureAsset> list) {
        for (var f = 0; f < 6; f++) {
            var t = getTextureForFace(f);
            list.addResource(this.layer, t);
            this.faceTextures[f] = t.getAbsolutePath();
        }
    }
}
