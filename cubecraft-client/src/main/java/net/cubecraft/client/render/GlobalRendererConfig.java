package net.cubecraft.client.render;

import com.google.gson.JsonObject;
import ink.flybird.fcommon.math.MathHelper;

public record GlobalRendererConfig(
        int clearColor,
        int fogColor
) {
    static GlobalRendererConfig from(JsonObject obj) {
        return new GlobalRendererConfig(
                MathHelper.hex2Int(obj.get("clear_color").getAsString()),
                MathHelper.hex2Int(obj.get("fog_color").getAsString())
        );
    }
}
