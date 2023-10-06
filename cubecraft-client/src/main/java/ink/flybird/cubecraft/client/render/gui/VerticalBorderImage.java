package ink.flybird.cubecraft.client.render.gui;

import com.google.gson.*;
import ink.flybird.cubecraft.client.gui.node.Node;
import ink.flybird.fcommon.registry.TypeItem;
import ink.flybird.quantum3d_legacy.textures.Texture2D;

import java.lang.reflect.Type;

@TypeItem("vertical_border_image")
public final class VerticalBorderImage extends ImageComponentRendererPart {
    private final int border;

    public VerticalBorderImage(double x0, double x1, double y0, double y1, int border, String textureLocation) {
        super(x0, x1, y0, y1, textureLocation);
        this.border = border;
    }

    @Override
    public void render(Node node) {
        //todo
        Texture2D tex= this.getTexture();
        tex.bind();

        tex.unbind();
    }

    public static class JDeserializer implements JsonDeserializer<VerticalBorderImage> {
        @Override
        public VerticalBorderImage deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            JsonObject root=jsonElement.getAsJsonObject();
            return new VerticalBorderImage(
                    root.get("pos").getAsJsonArray().get(0).getAsDouble(),
                    root.get("pos").getAsJsonArray().get(1).getAsDouble(),
                    root.get("pos").getAsJsonArray().get(2).getAsDouble(),
                    root.get("pos").getAsJsonArray().get(3).getAsDouble(),
                    root.get("boarder").getAsInt(),
                    root.get("loc").getAsString());
        }
    }
}
