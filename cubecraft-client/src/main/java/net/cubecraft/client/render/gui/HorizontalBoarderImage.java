package net.cubecraft.client.render.gui;

import com.google.gson.*;
import ink.flybird.quantum3d_legacy.ShapeRenderer;
import ink.flybird.quantum3d_legacy.draw.LegacyVertexBuilder;
import ink.flybird.quantum3d_legacy.draw.VertexBuilderAllocator;
import ink.flybird.quantum3d_legacy.textures.Texture2D;
import me.gb2022.commons.registry.TypeItem;
import net.cubecraft.client.gui.node.Node;
import net.cubecraft.client.util.DeserializedConstructor;

@TypeItem("horizontal_border_image")
public final class HorizontalBoarderImage extends ImageComponentRendererPart {
    private final int border;

    public HorizontalBoarderImage(double x0, double x1, double y0, double y1, int border, String textureLocation) {
        super(x0, x1, y0, y1, textureLocation);
        this.border = border;
    }

    @DeserializedConstructor
    public HorizontalBoarderImage(JsonObject root) {
        this(
                root.get("pos").getAsJsonArray().get(0).getAsDouble(),
                root.get("pos").getAsJsonArray().get(1).getAsDouble(),
                root.get("pos").getAsJsonArray().get(2).getAsDouble(),
                root.get("pos").getAsJsonArray().get(3).getAsDouble(),
                root.get("boarder").getAsInt(),
                root.get("loc").getAsString()
        );
    }

    @Override
    public void render(Node node) {
        int x = (int) (node.getLayout().getAbsoluteX() + x0 * node.getLayout().getAbsoluteWidth());
        int y = (int) (node.getLayout().getAbsoluteY() + y0 * node.getLayout().getAbsoluteHeight());
        int z = node.getLayout().layer;
        int w = (int) (node.getLayout().getAbsoluteWidth() * (x1 - x0));
        int h = (int) (node.getLayout().getAbsoluteHeight() * (y1 - y0));

        Texture2D tex = this.getTexture();
        double tbh = (double) this.border / tex.getWidth();

        int x0In = x + this.border, x1In = x + w - this.border, x1Out = x + w;
        LegacyVertexBuilder builder = VertexBuilderAllocator.createByPrefer(12);
        builder.begin();
        ShapeRenderer.drawRectUV(builder, x, x0In, y, y + h, z, 0, tbh, 0, 1);
        ShapeRenderer.drawRectUV(builder, x0In, x1In, y, y + h, z, tbh, 1 - tbh, 0, 1);
        ShapeRenderer.drawRectUV(builder, x1In, x1Out, y, y + h, z, 1 - tbh, 1, 0, 1);
        builder.end();
        builder.uploadPointer();
        builder.free();
    }
}
