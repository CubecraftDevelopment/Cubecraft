package net.cubecraft.client.render.gui;

import com.google.gson.*;
import net.cubecraft.client.context.ClientGUIContext;
import net.cubecraft.client.gui.base.Text;
import net.cubecraft.client.gui.node.Node;
import net.cubecraft.client.render.renderer.ComponentRendererPart;
import me.gb2022.commons.registry.TypeItem;
import net.cubecraft.client.util.DeserializedConstructor;

@TypeItem("font")
public record Font(double x, double y, int size, int col, int yOffset, String query,float iconModifier) implements ComponentRendererPart {

    @DeserializedConstructor
    public Font(JsonObject root){
        this(root.get("pos").getAsJsonArray().get(0).getAsDouble(),
                root.get("pos").getAsJsonArray().get(1).getAsDouble(),
                root.get("size").getAsInt(),
                root.has("color")?root.get("color").getAsInt():0xFFFFFF,
                root.get("line_offset").getAsInt(),
                root.get("binding").getAsString(),
                root.has("icon_offset")?root.get("icon_offset").getAsFloat():1.0f
        );
    }

    @Override
    public void render(Node node) {
        int x= (int) (node.getLayout().getAbsoluteX() +this.x* node.getLayout().getAbsoluteWidth());
        int y= (int) (node.getLayout().getAbsoluteY() +this.y* node.getLayout().getAbsoluteHeight());
        Text text=node.queryText(query);
        int color=text.getColor()!=0xFFFFFF?text.getColor():col;
        if(text.isIcon()){
            ClientGUIContext.ICON_FONT_RENDERER.render(text.getText(),x, (int) (y+yOffset-size*((iconModifier-1)/2)),color, (int) (size*(iconModifier)), 0, text.getAlignment());
        }else{
            ClientGUIContext.FONT_RENDERER.render(text.getText(),x,y+yOffset, color, size, 0, text.getAlignment());
        }
    }
}
