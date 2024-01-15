package net.cubecraft.client.render.model.component;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public final class Face {
    private final String texture;
    private final String color;
    private final float u0;
    private final float u1;
    private final float v0;
    private final float v1;

    public Face(JsonObject obj) {
        this.texture = obj.get("texture").getAsString();
        this.color = obj.has("color") ? obj.get("color").getAsString() : "_default";

        JsonArray array = obj.get("uv").getAsJsonArray();
        this.u0 = array.get(0).getAsFloat();
        this.u1 = array.get(1).getAsFloat();
        this.v0 = array.get(2).getAsFloat();
        this.v1 = array.get(3).getAsFloat();
    }

    void render() {

    }

    public float getU0() {
        return u0;
    }

    public float getU1() {
        return u1;
    }

    public float getV0() {
        return v0;
    }

    public float getV1() {
        return v1;
    }

    public String getColor() {
        return color;
    }

    public String getTexture() {
        return texture;
    }
}
