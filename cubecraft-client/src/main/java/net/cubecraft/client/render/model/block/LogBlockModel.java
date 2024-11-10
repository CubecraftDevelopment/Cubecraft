package net.cubecraft.client.render.model.block;

import com.google.gson.JsonObject;
import me.gb2022.commons.registry.TypeItem;
import net.cubecraft.client.render.model.ColorMap;
import net.cubecraft.client.render.model.CullingPredication;
import net.cubecraft.client.resource.TextureAsset;
import net.cubecraft.client.util.DeserializedConstructor;

@TypeItem("cubecraft:log")
public final class LogBlockModel extends BlockShapedModel {
    private final TextureAsset side;
    private final TextureAsset top;

    public LogBlockModel(CullingPredication culling, ColorMap color, String layer, TextureAsset side, TextureAsset top) {
        super(culling, color, layer);
        this.side = side;
        this.top = top;
    }

    @DeserializedConstructor
    public LogBlockModel(JsonObject json) {
        super(json);
        this.side = new TextureAsset(json.get("side_texture").getAsString());
        this.top = new TextureAsset(json.get("top_texture").getAsString());
    }

    @Override
    public TextureAsset getTextureForFace(int f) {
        if (f == 0 || f == 1) {
            return this.top;
        } else {
            return this.side;
        }
    }

    @Override
    public String getParticleTexture() {
        return this.side.getAbsolutePath();
    }
}
