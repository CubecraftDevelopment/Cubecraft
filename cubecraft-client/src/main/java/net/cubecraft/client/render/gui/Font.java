package net.cubecraft.client.render.gui;

import com.google.gson.JsonObject;
import me.gb2022.commons.registry.TypeItem;
import net.cubecraft.client.gui.base.Text;
import net.cubecraft.client.gui.font.FontRenderer;
import net.cubecraft.client.gui.node.Node;
import net.cubecraft.util.DeserializedConstructor;

@TypeItem("font")
public record Font(double x, double y, int size, int col, int yOffset, String query, float iconModifier) implements ComponentRendererPart {

    @DeserializedConstructor
    public Font(JsonObject root) {
        this(root.get("pos").getAsJsonArray().get(0).getAsDouble(),
             root.get("pos").getAsJsonArray().get(1).getAsDouble(),
             root.get("size").getAsInt(),
             root.has("color") ? root.get("color").getAsInt() : 0xFFFFFF,
             root.get("line_offset").getAsInt(),
             root.get("binding").getAsString(),
             root.has("icon_offset") ? root.get("icon_offset").getAsFloat() : 1.0f
        );
    }

    @Override
    public void render(Node node) {
        int x = (int) (node.getLayout().getAbsoluteX() + this.x * node.getLayout().getAbsoluteWidth());
        int y = (int) (node.getLayout().getAbsoluteY() + this.y * node.getLayout().getAbsoluteHeight());
        Text text = node.queryText(query);

        text.getText().color(col);

        if (text.isIcon()) {
            FontRenderer.icon().render(text.getText(),
                                                   x,
                                                   (int) (y + yOffset - size * ((iconModifier - 1) / 2)),
                                                   0,
                                                   text.getAlignment()
            );
        } else {
            FontRenderer.ttf().render(text.getText(), x, y + yOffset, 0, text.getAlignment());
        }
    }
}
