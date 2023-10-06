package ink.flybird.cubecraft.client.gui;

import com.google.gson.*;
import ink.flybird.cubecraft.client.gui.node.Node;
import ink.flybird.cubecraft.client.render.renderer.ComponentRendererPart;
import ink.flybird.cubecraft.client.resource.TextureAsset;
import ink.flybird.fcommon.container.CollectionUtil;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public final class ComponentRenderer{
    private final HashMap<String, ComponentRendererPart[]> renderers;

    public ComponentRenderer(HashMap<String, ComponentRendererPart[]> renderers) {
        this.renderers = renderers;
    }

    public void render(Node node){
        ComponentRendererPart[] list=this.renderers.get(node.getStatement());
        if(list!=null) {
            for (ComponentRendererPart componentPartRenderer :list) {
                componentPartRenderer.render(node);
            }
        }
    }

    public void initializeModel(Set<TextureAsset> loc){
        CollectionUtil.iterateMap(this.renderers,((key, item) -> {
            for (ComponentRendererPart renderer:item){
                renderer.initializeRenderer(loc);
            }
        }));
    }

    public static class JDeserializer implements JsonDeserializer<ComponentRenderer>{
        @Override
        public ComponentRenderer deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            HashMap<String, ComponentRendererPart[]> renderers=new HashMap<>();
            for (JsonElement e:jsonElement.getAsJsonArray()) {
                ArrayList<ComponentRendererPart> rendererList=new ArrayList<>();
                JsonObject obj=e.getAsJsonObject();
                for (JsonElement ele : obj.get("components").getAsJsonArray()) {
                    rendererList.add(jsonDeserializationContext.deserialize(ele.getAsJsonObject(), ComponentRendererPart.class));
                }
                renderers.put(obj.get("state").getAsString(),rendererList.toArray(new ComponentRendererPart[0]));
            }
            return new ComponentRenderer(renderers);
        }
    }
}
