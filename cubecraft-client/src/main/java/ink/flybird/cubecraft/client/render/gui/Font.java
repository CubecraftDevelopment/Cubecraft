package ink.flybird.cubecraft.client.render.gui;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import ink.flybird.cubecraft.client.ClientSharedContext;
import ink.flybird.cubecraft.client.gui.base.Text;
import ink.flybird.cubecraft.client.gui.node.Node;
import ink.flybird.cubecraft.client.render.renderer.ComponentRendererPart;
import ink.flybird.cubecraft.client.resource.TextureAsset;
import ink.flybird.fcommon.registry.TypeItem;

import java.lang.reflect.Type;
import java.util.Set;

@TypeItem("font")
public record Font(double x, double y, int size, int col, int yOffset, String query,float iconModifier) implements ComponentRendererPart {
    @Override
    public void render(Node node) {
        int x= (int) (node.getLayout().getAbsoluteX() +this.x* node.getLayout().getAbsoluteWidth());
        int y= (int) (node.getLayout().getAbsoluteY() +this.y* node.getLayout().getAbsoluteHeight());
        Text text=node.queryText(query);
        int color=text.getColor()!=0xFFFFFF?text.getColor():col;
        if(text.isIcon()){
            ClientSharedContext.ICON_FONT_RENDERER.render(text.getText(),x, (int) (y+yOffset-size*((iconModifier-1)/2)),color, (int) (size*(iconModifier)), 0, text.getAlignment());
        }else{
            ClientSharedContext.SMOOTH_FONT_RENDERER.render(text.getText(),x,y+yOffset, color, size, 0, text.getAlignment());
        }
    }

    @Override
    public void initializeRenderer(Set<TextureAsset> loc) {

    }

    public static class JDeserializer implements JsonDeserializer<Font>{
        @Override
        public Font deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            int col=jsonElement.getAsJsonObject().has("color")?jsonElement.getAsJsonObject().get("color").getAsInt():0xFFFFFF;
            return new Font(
                    jsonElement.getAsJsonObject().get("pos").getAsJsonArray().get(0).getAsDouble(),
                    jsonElement.getAsJsonObject().get("pos").getAsJsonArray().get(1).getAsDouble(),
                    jsonElement.getAsJsonObject().get("size").getAsInt(),
                    col,
                    jsonElement.getAsJsonObject().get("line_offset").getAsInt(),
                    jsonElement.getAsJsonObject().get("binding").getAsString(),
                    jsonElement.getAsJsonObject().has("icon_offset")?jsonElement.getAsJsonObject().get("icon_offset").getAsFloat():1.0f
            );
        }
    }
}
