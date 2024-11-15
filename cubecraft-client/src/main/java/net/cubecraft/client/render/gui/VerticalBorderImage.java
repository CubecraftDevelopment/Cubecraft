package net.cubecraft.client.render.gui;

import com.google.gson.*;
import me.gb2022.quantum3d.texture.Texture2D;
import me.gb2022.commons.registry.TypeItem;
import net.cubecraft.client.gui.node.Node;
import net.cubecraft.client.util.DeserializedConstructor;

@TypeItem("vertical_border_image")
public final class VerticalBorderImage extends ImageComponentRendererPart {
    private final int border;

    public VerticalBorderImage(double x0, double x1, double y0, double y1, int border, String textureLocation) {
        super(x0, x1, y0, y1, textureLocation);
        this.border = border;
    }

    @DeserializedConstructor
    public VerticalBorderImage(JsonObject root) {
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
        //todo
        Texture2D tex = this.getTexture();
        tex.bind();

        tex.unbind();
    }
}
