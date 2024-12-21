package net.cubecraft.client.render.model.block;

import com.google.gson.*;
import me.gb2022.commons.container.Pair;
import me.gb2022.commons.registry.ConstructingMap;
import me.gb2022.quantum3d.render.vertex.VertexBuilder;
import net.cubecraft.client.ClientContext;
import net.cubecraft.client.render.block.IBlockRenderer;
import net.cubecraft.client.render.chunk.container.ChunkLayerContainerFactory;
import net.cubecraft.client.render.model.ModelManager;
import net.cubecraft.client.render.model.object.Model;
import net.cubecraft.client.resource.ModelAsset;
import net.cubecraft.resource.ResourceLocation;
import net.cubecraft.util.register.Registered;
import net.cubecraft.world.BlockAccessor;
import net.cubecraft.world.block.access.BlockAccess;

import java.lang.reflect.Type;
import java.util.ArrayList;


public abstract class BlockModel implements Model, IBlockRenderer {
    public static final ModelManager<BlockModel> REGISTRY = new ModelManager<>(BlockModel.class, new ModelAsset("cubecraft:/block/fallback.json"));
    public static final ConstructingMap<BlockModel> COMPONENTS = new ConstructingMap<>(BlockModel.class, JsonObject.class);

    static {
        COMPONENTS.registerItem(SimpleBlockModel.class);
        COMPONENTS.registerItem(LogBlockModel.class);
        COMPONENTS.registerItem(ComponentBlockModel.class);
    }

    public abstract void render(BlockAccess block, BlockAccessor accessor, Registered<ChunkLayerContainerFactory.Provider> layer, int face, float x, float y, float z, VertexBuilder builder);

    public abstract String getParticleTexture();

    public static class JDeserializer implements JsonDeserializer<BlockModel> {
        public JsonElement processReplacement(JsonElement element, ArrayList<Pair<String, String>> list) {
            if (!element.getAsJsonObject().has("cover_json")) {
                return element;
            }
            JsonObject root = element.getAsJsonObject().get("cover_json").getAsJsonObject();
            String src = ClientContext.RESOURCE_MANAGER.getResource(ResourceLocation.blockModel(root.get("import").getAsString()))
                    .getAsText();

            JsonArray arr = root.get("replacement").getAsJsonArray();
            for (JsonElement e : arr) {
                list.add(new Pair<>(
                                 e.getAsJsonObject().get("from").getAsString(),
                                 e.getAsJsonObject().get("to").getAsString()
                         )
                );
            }

            if (!JsonParser.parseString(src).getAsJsonObject().has("cover_json")) {
                for (Pair<String, String> p : list) {
                    src = src.replace(p.getLeft(), p.getRight());
                }
                return JsonParser.parseString(src);
            }
            return processReplacement(JsonParser.parseString(src), list);
        }

        @Override
        public BlockModel deserialize(JsonElement element, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            JsonObject root = processReplacement(element, new ArrayList<>()).getAsJsonObject();
            return COMPONENTS.create(root.get("type").getAsString(), root);
        }
    }
}
