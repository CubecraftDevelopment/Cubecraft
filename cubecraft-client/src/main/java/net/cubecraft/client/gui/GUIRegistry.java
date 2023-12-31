package net.cubecraft.client.gui;

import com.google.gson.JsonDeserializer;
import net.cubecraft.SharedContext;
import net.cubecraft.client.ClientSharedContext;
import net.cubecraft.client.gui.font.SmoothedFontRenderer;
import net.cubecraft.client.gui.layout.Border;
import net.cubecraft.client.gui.layout.Layout;
import net.cubecraft.client.gui.node.Node;
import net.cubecraft.client.render.renderer.ComponentRendererPart;
import net.cubecraft.client.resource.ModelAsset;
import ink.flybird.fcommon.registry.ConstructingMap;
import ink.flybird.fcommon.registry.TypeItem;
import org.w3c.dom.Element;

import java.util.HashMap;
import java.util.Objects;

public interface GUIRegistry {
    ConstructingMap<Node> NODE = new ConstructingMap<>(Node.class);
    ConstructingMap<Layout> LAYOUT = new ConstructingMap<>(Layout.class);
    HashMap<Class<? extends Node>, ComponentRenderer> COMPONENT_RENDERER = new HashMap<>();
    HashMap<String, Class<? extends ComponentRendererPart>> COMPONENT_RENDERER_PART = new HashMap<>();

    HashMap<String, Class<? extends Layout>> layoutClassMapping = new HashMap<>();
    SmoothedFontRenderer SMOOTH_FONT_RENDERER = new SmoothedFontRenderer();
    SmoothedFontRenderer ICON_FONT_RENDERER = new SmoothedFontRenderer();

    static void registerComponent(Class<? extends Node> clazz) {
        String id = clazz.getDeclaredAnnotation(TypeItem.class).value();
        NODE.registerItem(id, clazz);
        ModelAsset asset = new ModelAsset("cubecraft:/ui/" + id + ".json");
        String resID = "cubecraft:" + id + "_render_controller";
        ClientSharedContext.RESOURCE_MANAGER.registerResource("default",resID,asset);
        ClientSharedContext.RESOURCE_MANAGER.loadResource(asset);
        ComponentRenderer renderer = SharedContext.createJsonReader().fromJson(asset.getRawText(), ComponentRenderer.class);
        COMPONENT_RENDERER.put(clazz, renderer);
    }

    static void registerRendererComponentPart(Class<? extends ComponentRendererPart> clazz, JsonDeserializer<?> deserializer) {
        String id = clazz.getDeclaredAnnotation(TypeItem.class).value();
        COMPONENT_RENDERER_PART.put(id, clazz);
        SharedContext.GSON_BUILDER.registerTypeAdapter(clazz, deserializer);
    }

    static Layout createLayout(String content, String border) {
        String type = content.split("/")[0];
        String cont = content.split("/")[1];

        Layout layout = LAYOUT.create(type);

        layout.initialize(cont.split(","));

        if (Objects.equals(border, "")) {
            return layout;
        }

        layout.setBorder(new Border(
                Integer.parseInt(border.split(",")[0]),
                Integer.parseInt(border.split(",")[1]),
                Integer.parseInt(border.split(",")[3]),
                Integer.parseInt(border.split(",")[2])
        ));
        return layout;
    }

    static Node createNode(String type, Element element) {
        Node n = NODE.create(type);
        n.init(element);

        return n;
    }

    static void initialize(){
        SharedContext.GSON_BUILDER.registerTypeAdapter(ComponentRenderer.class, new ComponentRenderer.JDeserializer());
        SharedContext.GSON_BUILDER.registerTypeAdapter(ComponentRendererPart.class, new ComponentRendererPart.JDeserializer());
    }
}
