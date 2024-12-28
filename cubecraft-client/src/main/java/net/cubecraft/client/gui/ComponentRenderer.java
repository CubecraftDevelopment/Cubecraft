package net.cubecraft.client.gui;

import com.google.gson.*;
import me.gb2022.commons.container.CollectionUtil;
import net.cubecraft.client.gui.node.Node;
import net.cubecraft.client.render.gui.ComponentRendererPart;
import net.cubecraft.client.resource.TextureAsset;
import org.joml.Vector2i;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public final class ComponentRenderer {
    private final HashMap<String, ComponentRendererPart[]> renderers;
    private final Map<String, Vector2i> childOffsets;

    public ComponentRenderer(HashMap<String, ComponentRendererPart[]> renderers, Map<String, Vector2i> childOffsets) {
        this.renderers = renderers;
        this.childOffsets = childOffsets;
    }

    public void render(Node node) {
        if(node.getStatement().contains("none")){
            return;
        }

        ComponentRendererPart[] list = this.renderers.get(node.getStatement());
        if (list != null) {
            for (ComponentRendererPart componentPartRenderer : list) {
                componentPartRenderer.render(node);
            }
        }
    }

    public void initializeModel(Set<TextureAsset> loc) {
        CollectionUtil.iterateMap(this.renderers, ((key, item) -> {
            for (ComponentRendererPart renderer : item) {
                renderer.initializeRenderer(loc);
            }
        }));
    }

    public Vector2i getOffset(Node node) {
        return this.childOffsets.computeIfAbsent(node.getStatement(), (k) -> new Vector2i(0, 0));
    }

    public static class JDeserializer implements JsonDeserializer<ComponentRenderer> {
        @Override
        public ComponentRenderer deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext ctx) throws JsonParseException {
            var renderers = new HashMap<String, ComponentRendererPart[]>();
            var offsets = new HashMap<String, Vector2i>();

            if (jsonElement.isJsonArray()) {
                for (JsonElement e : jsonElement.getAsJsonArray()) {
                    ArrayList<ComponentRendererPart> rendererList = new ArrayList<>();
                    JsonObject obj = e.getAsJsonObject();
                    for (JsonElement ele : obj.get("components").getAsJsonArray()) {
                        rendererList.add(ctx.deserialize(ele.getAsJsonObject(), ComponentRendererPart.class));
                    }
                    renderers.put(obj.get("state").getAsString(), rendererList.toArray(new ComponentRendererPart[0]));
                }


                return new ComponentRenderer(renderers, offsets);
            }

            var dom = jsonElement.getAsJsonObject();

            for (var id : dom.keySet()) {
                var node = dom.get(id).getAsJsonObject();
                var components = new ArrayList<ComponentRendererPart>();

                if (node.has("child_offset_x")) {
                    offsets.computeIfAbsent(id, (k) -> new Vector2i(0, 0)).x = node.get("child_offset_x").getAsInt();
                }

                if (node.has("child_offset_y")) {
                    offsets.computeIfAbsent(id, (k) -> new Vector2i(0, 0)).y = node.get("child_offset_y").getAsInt();
                }

                for (var ele : node.get("components").getAsJsonArray()) {
                    components.add(ctx.deserialize(ele.getAsJsonObject(), ComponentRendererPart.class));
                }

                renderers.put(id, components.toArray(new ComponentRendererPart[0]));
            }

            return new ComponentRenderer(renderers, offsets);
        }
    }
}
