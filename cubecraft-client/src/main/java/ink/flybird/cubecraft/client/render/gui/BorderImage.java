package ink.flybird.cubecraft.client.render.gui;

import com.google.gson.*;
import ink.flybird.cubecraft.client.gui.node.Node;
import ink.flybird.fcommon.registry.TypeItem;
import ink.flybird.quantum3d_legacy.ShapeRenderer;
import ink.flybird.quantum3d_legacy.draw.VertexBuilder;
import ink.flybird.quantum3d_legacy.draw.VertexBuilderAllocator;
import ink.flybird.quantum3d_legacy.textures.Texture2D;

import java.lang.reflect.Type;

@TypeItem("border_image")
public final class BorderImage extends ImageComponentRendererPart {
    private final int boarderH;
    private final int boarderV;

    public BorderImage(double x0, double x1, double y0, double y1, int boarderH, int boarderV, String textureLocation) {
        super(x0, x1, y0, y1, textureLocation);
        this.boarderH = boarderH;
        this.boarderV = boarderV;
    }

    @Override
    public void render(Node node) {
        int x = (int) (node.getLayout().getAbsoluteX() + x0 * node.getLayout().getAbsoluteWidth());
        int y = (int) (node.getLayout().getAbsoluteY() + y0 * node.getLayout().getAbsoluteHeight());
        int w = (int) (node.getLayout().getAbsoluteWidth() * (x1 - x0));
        int h = (int) (node.getLayout().getAbsoluteHeight() * (y1 - y0));

        Texture2D tex = this.getTexture();

        double tbh = (double) boarderH / tex.getWidth();
        double tbv = (double) boarderV / tex.getHeight();

        int b = (int) (tbh * h);
        int x0In = x + b, x1In = x + w - b, x1Out = x + w;
        int y0In = (int) (y + tbv * h), y1In = (int) (y + h - tbv * h), y1Out = y + h;

        tex.bind();
        VertexBuilder builder = VertexBuilderAllocator.createByPrefer(36);
        builder.begin();

        //corner
        ShapeRenderer.drawRectUV(builder, x, x0In, y, y0In, 0, 0, tbh, 0, tbv);
        ShapeRenderer.drawRectUV(builder, x, x0In, y1In, y1Out, 0, 0, tbh, 1 - tbv, 1);
        ShapeRenderer.drawRectUV(builder, x1In, x1Out, y, y0In, 0, 1 - tbh, 1, 0, tbv);
        ShapeRenderer.drawRectUV(builder, x1In, x1Out, y1In, y1Out, 0, 1 - tbh, 1, 1 - tbv, 1);

        //edge
        ShapeRenderer.drawRectUV(builder, x0In, x1In, y, y0In, 0, tbh, 1 - tbh, 0, tbv);
        ShapeRenderer.drawRectUV(builder, x0In, x1In, y1In, y1Out, 0, tbh, 1 - tbh, 1 - tbv, 1);
        ShapeRenderer.drawRectUV(builder, x, x0In, y0In, y1In, 0, 0, tbh, tbv, 1 - tbv);
        ShapeRenderer.drawRectUV(builder, x1In, x1Out, y0In, y1In, 0, 1 - tbh, 1, tbv, 1 - tbv);

        //center
        ShapeRenderer.drawRectUV(builder, x0In, x1In, y0In, y1In, 0, tbh, 1 - tbh, tbv, 1 - tbv);

        builder.end();
        builder.uploadPointer();
        builder.free();
    }

    public static class JDeserializer implements JsonDeserializer<BorderImage> {
        @Override
        public BorderImage deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            JsonObject root = jsonElement.getAsJsonObject();
            return new BorderImage(
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
