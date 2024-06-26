package net.cubecraft.client.render.model.block;

import com.google.gson.*;
import ink.flybird.quantum3d_legacy.draw.VertexBuilder;
import me.gb2022.commons.container.Pair;
import me.gb2022.commons.registry.ConstructingMap;
import net.cubecraft.client.ClientSharedContext;
import net.cubecraft.client.render.model.object.Model;
import net.cubecraft.resource.ResourceLocation;
import net.cubecraft.world.IWorld;
import net.cubecraft.world.block.access.IBlockAccess;

import java.lang.reflect.Type;
import java.util.ArrayList;


public abstract class BlockModel implements Model {
    public static final ConstructingMap<BlockModel> REGISTRY = new ConstructingMap<>(BlockModel.class, JsonObject.class);

    static {
        REGISTRY.registerItem(SimpleBlockModel.class);
        REGISTRY.registerItem(ComponentBlockModel.class);
    }

    public void renderAsItem(VertexBuilder builder, double renderX, double renderY, double renderZ) {

    }

    public abstract void render(IBlockAccess blockAccess, VertexBuilder builder, String layer, IWorld world, double renderX, double renderY, double renderZ);

    public abstract String getParticleTexture();


    public static class JDeserializer implements JsonDeserializer<BlockModel> {
        public JsonElement processReplacement(JsonElement element, ArrayList<Pair<String, String>> list) {
            if (!element.getAsJsonObject().has("cover_json")) {
                return element;
            }
            JsonObject root = element.getAsJsonObject().get("cover_json").getAsJsonObject();
            String src = ClientSharedContext.RESOURCE_MANAGER.getResource(ResourceLocation.blockModel(root.get("import").getAsString())).getAsText();

            JsonArray arr = root.get("replacement").getAsJsonArray();
            for (JsonElement e : arr) {
                list.add(new Pair<>(
                        e.getAsJsonObject().get("from").getAsString(),
                        e.getAsJsonObject().get("to").getAsString())
                );
            }

            if (!JsonParser.parseString(src).getAsJsonObject().has("cover_json")) {
                for (Pair<String, String> p : list) {
                    src = src.replace(p.t1(), p.t2());
                }
                return JsonParser.parseString(src);
            }
            return processReplacement(JsonParser.parseString(src), list);
        }

        @Override
        public BlockModel deserialize(JsonElement element, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            JsonObject root = processReplacement(element, new ArrayList<>()).getAsJsonObject();
            return REGISTRY.create(root.get("type").getAsString(), root);
        }
    }
}
