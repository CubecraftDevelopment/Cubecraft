package ink.flybird.cubecraft.client.internal.renderer.gui;

import com.google.gson.*;
import ink.flybird.cubecraft.client.ClientRenderContext;
import ink.flybird.cubecraft.client.gui.node.Node;
import ink.flybird.cubecraft.client.render.renderer.IComponentPartRenderer;
import ink.flybird.cubecraft.client.resources.ResourceLocation;
import ink.flybird.quantum3d.textures.Texture2D;

import java.lang.reflect.Type;
import java.util.List;

public record VerticalBorderImage(double x0,double x1,double y0,double y1,int boarder,String loc) implements IComponentPartRenderer {
    @Override
    public void render(Node node) {
        Texture2D tex= ClientRenderContext.TEXTURE.getTexture2DContainer().get(ResourceLocation.uiTexture(this.loc.split(":")[0],this.loc.split(":")[1]).format());
    }

    @Override
    public void initializeRenderer(List<ResourceLocation> loc) {
        loc.add(ResourceLocation.uiTexture(this.loc.split(":")[0],this.loc.split(":")[1]));
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
