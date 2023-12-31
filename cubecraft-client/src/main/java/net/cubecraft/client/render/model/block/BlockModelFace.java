package net.cubecraft.client.render.model.block;

import com.google.gson.*;
import net.cubecraft.client.resource.TextureAsset;

import java.lang.reflect.Type;

public record BlockModelFace(TextureAsset texture, float u0, float u1, float v0, float v1, String color,
                             CullingMethod culling) {

    public BlockModelFace(JsonObject json) {
        this(
                new TextureAsset(json.get("texture").getAsString()),
                json.get("uv").getAsJsonArray().get(0).getAsFloat(),
                json.get("uv").getAsJsonArray().get(1).getAsFloat(),
                json.get("uv").getAsJsonArray().get(2).getAsFloat(),
                json.get("uv").getAsJsonArray().get(3).getAsFloat(),
                json.has("color") ? json.get("color").getAsString() : "cubecraft:default",
                CullingMethod.from(json.has("culling") ? json.get("culling").getAsString() : "solid")
        );
    }

    public static class JDeserializer implements JsonDeserializer<BlockModelFace> {
        @Override
        public BlockModelFace deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            return new BlockModelFace(jsonElement.getAsJsonObject());
        }
    }
}
