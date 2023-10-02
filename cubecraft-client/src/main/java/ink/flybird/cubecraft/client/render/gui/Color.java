package ink.flybird.cubecraft.client.render.gui;

import com.google.gson.*;
import ink.flybird.cubecraft.client.gui.node.Node;
import ink.flybird.cubecraft.client.render.renderer.IComponentPartRenderer;
import ink.flybird.cubecraft.client.resource.TextureAsset;
import ink.flybird.quantum3d_legacy.GLUtil;
import ink.flybird.quantum3d_legacy.ShapeRenderer;

import java.lang.reflect.Type;
import java.util.Set;

public record Color(double x0, double x1, double y0, double y1, int r, int g, int b, int a)implements IComponentPartRenderer {
    @Override
    public void render(Node node) {
        int x= (int) (node.getLayout().getAbsoluteX() +x0* node.getLayout().getAbsoluteWidth());
        int y= (int) (node.getLayout().getAbsoluteY() +y0* node.getLayout().getAbsoluteHeight());
        int z=node.getLayout().layer;
        int w= (int) (node.getLayout().getAbsoluteWidth() *(x1-x0));
        int h= (int) (node.getLayout().getAbsoluteHeight() *(y1-y0));


        ShapeRenderer.setColor(r,g,b,a);
        ShapeRenderer.drawRect(x,x+w,y,y+h,z,z);
        ShapeRenderer.setColor(256,256,256,256);

        GLUtil.enableBlend();
        GLUtil.enableDepthTest();
    }

    @Override
    public void initializeRenderer(Set<TextureAsset> loc) {

    }

    public static class JDeserializer implements JsonDeserializer<Color>{
        @Override
        public Color deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            JsonObject root=jsonElement.getAsJsonObject();
            return new Color(
                    root.get("pos").getAsJsonArray().get(0).getAsDouble(),
                    root.get("pos").getAsJsonArray().get(1).getAsDouble(),
                    root.get("pos").getAsJsonArray().get(2).getAsDouble(),
                    root.get("pos").getAsJsonArray().get(3).getAsDouble(),
                    root.get("color").getAsJsonArray().get(0).getAsInt(),
                    root.get("color").getAsJsonArray().get(1).getAsInt(),
                    root.get("color").getAsJsonArray().get(2).getAsInt(),
                    root.get("color").getAsJsonArray().get(3).getAsInt()
            );
        }
    }
}
