package net.cubecraft.client.render.model.block.component;

import com.google.gson.JsonObject;
import me.gb2022.quantum3d.render.vertex.VertexBuilder;
import net.cubecraft.client.render.chunk.container.ChunkLayerContainerFactory;
import net.cubecraft.client.render.chunk.container.ChunkLayerContainers;
import net.cubecraft.client.render.model.CullingPredication;
import net.cubecraft.client.resource.TextureAsset;
import net.cubecraft.resource.MultiAssetContainer;
import net.cubecraft.util.register.Registered;
import net.cubecraft.world.BlockAccessor;
import net.cubecraft.world.block.access.BlockAccess;
import org.joml.Vector3d;
import org.joml.Vector3f;

public abstract class BlockModelComponent {
    protected final Vector3f start, end;
    protected final String layer;
    protected final int localizedLayer;

    protected BlockModelComponent(String layer, Vector3f start, Vector3f end) {
        this.start = start;
        this.end = end;

        this.localizedLayer = ChunkLayerContainers.REGISTRY.id(layer);
        this.layer = layer;
    }


    public Vector3f getStart() {
        return start;
    }

    public Vector3f getEnd() {
        return end;
    }

    public abstract void render(VertexBuilder builder, Registered<ChunkLayerContainerFactory.Provider> layer, int face, BlockAccessor world, BlockAccess block, double renderX, double renderY, double renderZ);

    public void provideTileMapItems(MultiAssetContainer<TextureAsset> list) {
    }

    public record BlockModelFace(TextureAsset texture, float u0, float u1, float v0, float v1, String color, CullingPredication culling) {

        public BlockModelFace(JsonObject json) {
            this(
                    new TextureAsset(json.get("texture").getAsString()),
                    json.get("uv").getAsJsonArray().get(0).getAsFloat(),
                    json.get("uv").getAsJsonArray().get(1).getAsFloat(),
                    json.get("uv").getAsJsonArray().get(2).getAsFloat(),
                    json.get("uv").getAsJsonArray().get(3).getAsFloat(),
                    json.has("color") ? json.get("color").getAsString() : "cubecraft:default",
                    json.has("culling") ? CullingPredication.REGISTRY.object(json.get("culling").getAsString()) : CullingPredication.F_SOLID
            );
        }
    }
}