package ink.flybird.cubecraft.client.render.model.block;

import com.google.gson.*;
import ink.flybird.cubecraft.client.ClientSharedContext;
import ink.flybird.cubecraft.client.resource.TextureAsset;
import ink.flybird.cubecraft.resource.ResourceLocation;
import ink.flybird.fcommon.container.Pair;
import ink.flybird.cubecraft.client.render.model.object.Model;
import ink.flybird.cubecraft.world.IWorld;
import ink.flybird.cubecraft.world.block.access.IBlockAccess;
import ink.flybird.quantum3d_legacy.draw.VertexBuilder;

import java.lang.reflect.Type;
import java.util.*;


public final class BlockModel implements Model {
    private final String particleTex;

    private final HashMap<String, ArrayList<BlockModelComponent>> statementModels;

    public BlockModel(HashMap<String, ArrayList<BlockModelComponent>> models, String particleTexture) {
        this.statementModels = models;
        this.particleTex = particleTexture;
    }

    public void render(IBlockAccess blockAccess, VertexBuilder builder, String layer, IWorld world, double renderX, double renderY, double renderZ) {
        Set<String> set = this.statementModels.keySet();
        for (String s : set) {
            if (!blockAccess.getBlock().queryBoolean(s, blockAccess)) {
                continue;
            }
            ArrayList<BlockModelComponent> components;
            components = this.statementModels.get(s);
            if (components == null) {
                components = this.statementModels.get("default");
            }
            for (BlockModelComponent component : components) {
                if (!Objects.equals(layer, component.getRenderLayer())) {
                    continue;
                }
                component.render(builder, layer, world, blockAccess, renderX, renderY, renderZ);
            }
        }
    }

    public void renderAsItem(VertexBuilder builder, double renderX, double renderY, double renderZ) {

    }

    @Override
    public void initializeModel(Set<TextureAsset> textureList) {
        for (String state : this.statementModels.keySet()) {
            for (BlockModelComponent component : this.statementModels.get(state)) {
                component.initializeModel(textureList);
            }
        }
    }


    @Deprecated
    public String getParticleTexture() {
        return !Objects.equals(this.particleTex, "cubecraft:_EMPTY_") ? ResourceLocation.blockTexture(this.particleTex).format() : null;
    }


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
            element = processReplacement(element, new ArrayList<>());

            HashMap<String, ArrayList<BlockModelComponent>> components = new HashMap<>();

            JsonArray models = element.getAsJsonObject().get("models").getAsJsonArray();
            for (JsonElement e : models) {
                JsonObject object = e.getAsJsonObject();
                JsonArray comp = object.get("components").getAsJsonArray();

                ArrayList<BlockModelComponent> component = new ArrayList<>();

                for (int i = 0; i < comp.size(); i++) {
                    component.add(jsonDeserializationContext.deserialize(comp.get(i), BlockModelComponent.class));
                }

                components.put(object.get("statement").getAsString(), component);

            }
            return new BlockModel(components, element.getAsJsonObject().get("particle_texture").getAsString());
        }
    }
}
