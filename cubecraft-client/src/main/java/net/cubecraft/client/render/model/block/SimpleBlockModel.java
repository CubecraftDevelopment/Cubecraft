package net.cubecraft.client.render.model.block;

import com.google.gson.JsonObject;
import me.gb2022.commons.registry.TypeItem;
import net.cubecraft.client.render.model.ColorMap;
import net.cubecraft.client.render.model.CullingPredication;
import net.cubecraft.client.resource.TextureAsset;
import net.cubecraft.util.DeserializedConstructor;


@TypeItem("cubecraft:simple_block")
public final class SimpleBlockModel extends BlockShapedModel {
    private final TextureAsset texture;

    public SimpleBlockModel(CullingPredication culling, ColorMap color, String layer, TextureAsset texture) {
        super(culling, color, layer);
        this.texture = texture;
    }

    @DeserializedConstructor
    public SimpleBlockModel(JsonObject json) {
        super(json);
        this.texture = new TextureAsset(json.get("texture").getAsString());
    }

    @Override
    public String getParticleTexture() {
        return this.texture.getAbsolutePath();
    }

    public TextureAsset getTextureForFace(int f) {
        return this.texture;
    }
}
