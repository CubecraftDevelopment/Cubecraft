package net.cubecraft.client.render.renderer;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import net.cubecraft.client.CubecraftClient;
import net.cubecraft.client.gui.node.Node;
import net.cubecraft.client.resource.TextureAsset;

import java.lang.reflect.Type;
import java.util.Set;

public interface ComponentRendererPart {
    void render(Node node);

    void initializeRenderer(Set<TextureAsset> loc);

    class JDeserializer implements JsonDeserializer<ComponentRendererPart>{
        @Override
        public ComponentRendererPart deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            String id= jsonElement.getAsJsonObject().get("type").getAsString();
            Class<?> clazz= CubecraftClient.CLIENT.getGuiManager().getRendererPartClass(id);
            return jsonDeserializationContext.deserialize(jsonElement,clazz);
        }
    }
}
