package net.cubecraft.client.render.gui;

import com.google.gson.*;
import ink.flybird.quantum3d_legacy.ShapeRenderer;
import ink.flybird.quantum3d_legacy.draw.VertexBuilder;
import ink.flybird.quantum3d_legacy.draw.VertexBuilderAllocator;
import ink.flybird.quantum3d_legacy.textures.Texture2D;
import me.gb2022.commons.registry.TypeItem;
import net.cubecraft.client.gui.node.Node;
import net.cubecraft.client.util.DeserializedConstructor;

import java.lang.reflect.Type;

@TypeItem("image_animation")
public final class ImageAnimation extends ImageComponentRendererPart {
    private final int interval;
    private final int frames;

    public ImageAnimation(double x0, double x1, double y0, double y1, String textureLocation, int interval, int frames) {
        super(x0, x1, y0, y1, textureLocation);
        this.interval = interval;
        this.frames = frames;
    }

    @DeserializedConstructor
    public ImageAnimation(JsonObject root) {
        this(
                root.get("pos").getAsJsonArray().get(0).getAsDouble(),
                root.get("pos").getAsJsonArray().get(1).getAsDouble(),
                root.get("pos").getAsJsonArray().get(2).getAsDouble(),
                root.get("pos").getAsJsonArray().get(3).getAsDouble(),
                root.get("loc").getAsString(),
                root.get("interval").getAsInt(),
                root.get("frames").getAsInt()
        );
    }

    @Override
    public void render(Node node) {
        int frame = (int) ((System.currentTimeMillis() / interval) % frames);
        int x = (int) (node.getLayout().getAbsoluteX() + x0 * node.getLayout().getAbsoluteWidth());
        int y = (int) (node.getLayout().getAbsoluteY() + y0 * node.getLayout().getAbsoluteHeight());
        int z = node.getLayout().layer;
        int w = (int) (node.getLayout().getAbsoluteWidth() * (x1 - x0));
        int h = (int) (node.getLayout().getAbsoluteHeight() * (y1 - y0));

        Texture2D tex = this.getTexture();

        //corner
        tex.bind();
        VertexBuilder builder = VertexBuilderAllocator.createByPrefer(36);
        builder.begin();

        ShapeRenderer.drawRectUV(builder, x, x + w, y, y + h, z, 0, 1, frame / (float) frames, frame / (float) frames + 1 / (float) frames);

        builder.end();
        builder.uploadPointer();
        builder.free();
    }
}
