package net.cubecraft.client.render.gui;

import com.google.gson.*;
import net.cubecraft.client.gui.node.Node;
import net.cubecraft.client.render.renderer.ComponentRendererPart;
import net.cubecraft.client.resource.TextureAsset;
import me.gb2022.commons.registry.TypeItem;
import ink.flybird.quantum3d_legacy.GLUtil;
import ink.flybird.quantum3d_legacy.ShapeRenderer;
import net.cubecraft.client.util.DeserializedConstructor;

import java.lang.reflect.Type;
import java.util.Set;

@TypeItem("color")
public record Color(double x0, double x1, double y0, double y1, int r, int g, int b, int a)implements ComponentRendererPart {
    @DeserializedConstructor
    public Color(JsonObject root){
        this(
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

    @Override
    public void render(Node node) {
        int x= (int) (node.getLayout().getAbsoluteX() +x0* node.getLayout().getAbsoluteWidth());
        int y= (int) (node.getLayout().getAbsoluteY() +y0* node.getLayout().getAbsoluteHeight());
        int z=node.getLayout().layer;
        int w= (int) (node.getLayout().getAbsoluteWidth() *(x1-x0));
        int h= (int) (node.getLayout().getAbsoluteHeight() *(y1-y0));


        ShapeRenderer.setColor(r,g,b,a);
        ShapeRenderer.drawRect(x,x+w,y,y+h,z,z);

        GLUtil.enableBlend();
        GLUtil.enableDepthTest();
    }
}
