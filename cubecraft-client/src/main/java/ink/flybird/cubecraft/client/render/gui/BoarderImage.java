package ink.flybird.cubecraft.client.render.gui;

import com.google.gson.*;
import ink.flybird.cubecraft.client.ClientRenderContext;
import ink.flybird.cubecraft.client.gui.node.Node;
import ink.flybird.cubecraft.client.render.renderer.IComponentPartRenderer;
import ink.flybird.cubecraft.client.resource.TextureAsset;
import ink.flybird.quantum3d_legacy.ShapeRenderer;
import ink.flybird.quantum3d_legacy.draw.VertexBuilder;
import ink.flybird.quantum3d_legacy.draw.VertexBuilderAllocator;
import ink.flybird.quantum3d_legacy.textures.Texture2D;

import java.lang.reflect.Type;
import java.util.Set;

public record BoarderImage(double x0, double x1, double y0, double y1, int boarderH, int boarderV,
                           String loc) implements IComponentPartRenderer {

    @Override
    public void render(Node node) {
        int x = (int) (node.getLayout().getAbsoluteX() + x0 * node.getLayout().getAbsoluteWidth());
        int y = (int) (node.getLayout().getAbsoluteY() + y0 * node.getLayout().getAbsoluteHeight());
        int w = (int) (node.getLayout().getAbsoluteWidth() * (x1 - x0));
        int h = (int) (node.getLayout().getAbsoluteHeight() * (y1 - y0));

        Texture2D tex = ClientRenderContext.TEXTURE.getTexture2DContainer().get(TextureAsset.format(this.loc));

        double tbh = (double) boarderH / tex.getWidth();
        double tbv = (double) boarderV / tex.getHeight();

        int b = (int) (tbh*h);
        int x0In = x + b, x1In = x + w - b, x1Out = x + w;
        int y0In = (int) (y + tbv * h), y1In = (int) (y + h - tbv * h), y1Out = y + h;

        //corner
        tex.bind();
        VertexBuilder builder = VertexBuilderAllocator.createByPrefer(36);
        builder.begin();

        ShapeRenderer.drawRectUV(builder, x, x0In, y, y0In, 0, 0, tbh, 0, tbv);
        ShapeRenderer.drawRectUV(builder, x, x0In, y1In, y1Out, 0, 0, tbh, 1 - tbv, 1);
        ShapeRenderer.drawRectUV(builder, x1In, x1Out, y, y0In, 0, 1 - tbh, 1, 0, tbv);
        ShapeRenderer.drawRectUV(builder, x1In, x1Out, y1In, y1Out, 0, 1 - tbh, 1, 1 - tbv, 1);

        ShapeRenderer.drawRectUV(builder, x0In, x1In, y, y0In, 0, tbh, 1 - tbh, 0, tbv);
        ShapeRenderer.drawRectUV(builder, x0In, x1In, y1In, y1Out, 0, tbh, 1 - tbh, 1 - tbv, 1);

        ShapeRenderer.drawRectUV(builder, x, x0In, y0In, y1In, 0, 0, tbh, tbv, 1 - tbv);
        ShapeRenderer.drawRectUV(builder, x1In, x1Out, y0In, y1In, 0, 1 - tbh, 1, tbv, 1 - tbv);

        ShapeRenderer.drawRectUV(builder, x0In, x1In, y0In, y1In, 0, tbh, 1 - tbh, tbv, 1 - tbv);

        builder.end();
        builder.uploadPointer();
        builder.free();
    }

    @Override
    public void initializeRenderer(Set<TextureAsset> loc) {
        loc.add(new TextureAsset(this.loc));
    }

    public static class JDeserializer implements JsonDeserializer<BoarderImage> {
        @Override
        public BoarderImage deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            JsonObject root = jsonElement.getAsJsonObject();
            return new BoarderImage(
                    root.get("pos").getAsJsonArray().get(0).getAsDouble(),
                    root.get("pos").getAsJsonArray().get(1).getAsDouble(),
                    root.get("pos").getAsJsonArray().get(2).getAsDouble(),
                    root.get("pos").getAsJsonArray().get(3).getAsDouble(),
                    root.get("boarderH").getAsInt(),
                    root.get("boarderV").getAsInt(),
                    root.get("loc").getAsString());
        }
    }
}
