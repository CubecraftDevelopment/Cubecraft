package ink.flybird.cubecraft.client.gui;

import com.google.gson.*;
import ink.flybird.cubecraft.client.gui.node.Node;
import ink.flybird.cubecraft.client.resources.ResourceLocation;
import ink.flybird.cubecraft.client.render.renderer.IComponentPartRenderer;
import ink.flybird.fcommon.container.CollectionUtil;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class ComponentRenderer{
    private final HashMap<String, IComponentPartRenderer[]> renderers;

    public ComponentRenderer(HashMap<String, IComponentPartRenderer[]> renderers) {
        this.renderers = renderers;
    }

    public void render(Node node){
        IComponentPartRenderer[] list=this.renderers.get(node.getStatement());
        if(list!=null) {
            for (IComponentPartRenderer componentPartRenderer :list) {
                componentPartRenderer.render(node);
            }
        }
    }

    public void initializeModel(List<ResourceLocation> loc){
        CollectionUtil.iterateMap(this.renderers,((key, item) -> {
            for (IComponentPartRenderer renderer:item){
                renderer.initializeRenderer(loc);
            }
        }));
    }

    public static class JDeserializer implements JsonDeserializer<ComponentRenderer>{
        @Override
        public ComponentRenderer deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            HashMap<String, IComponentPartRenderer[]> renderers=new HashMap<>();
            for (JsonElement e:jsonElement.getAsJsonArray()) {
                ArrayList<IComponentPartRenderer> rendererList=new ArrayList<>();
                JsonObject obj=e.getAsJsonObject();
                for (JsonElement ele : obj.get("components").getAsJsonArray()) {
                    rendererList.add(jsonDeserializationContext.deserialize(ele.getAsJsonObject(), IComponentPartRenderer.class));
                }
                renderers.put(obj.get("state").getAsString(),rendererList.toArray(new IComponentPartRenderer[0]));
            }
            return new ComponentRenderer(renderers);
        }
    }
}
