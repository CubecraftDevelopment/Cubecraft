package net.cubecraft.client.render.model.block;

import com.google.gson.*;
import ink.flybird.quantum3d_legacy.draw.VertexBuilder;
import me.gb2022.commons.registry.TypeItem;
import net.cubecraft.client.resource.TextureAsset;
import net.cubecraft.client.util.DeserializedConstructor;
import net.cubecraft.world.IWorld;
import net.cubecraft.world.block.access.IBlockAccess;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.Set;

@TypeItem("cubecraft:component_block")
public final class ComponentBlockModel extends BlockModel {
    private final String particleTex;
    private final HashMap<String, ArrayList<BlockModelComponent>> statementModels;


    @DeserializedConstructor
    public ComponentBlockModel(JsonObject obj) {
        HashMap<String, ArrayList<BlockModelComponent>> components = new HashMap<>();
        JsonArray models = obj.get("models").getAsJsonArray();
        String particleTexture = obj.get("particle_texture").getAsString();

        for (JsonElement e : models) {
            JsonObject object = e.getAsJsonObject();
            JsonArray comp = object.get("components").getAsJsonArray();

            ArrayList<BlockModelComponent> component = new ArrayList<>();

            for (int i = 0; i < comp.size(); i++) {
                component.add(new BlockModelComponent.Cube(comp.get(i).getAsJsonObject()));
            }

            components.put(object.get("statement").getAsString(), component);
        }

        this.statementModels = components;
        this.particleTex = particleTexture;
    }


    public ComponentBlockModel(HashMap<String, ArrayList<BlockModelComponent>> models, String particleTexture) {
        this.statementModels = models;
        this.particleTex = particleTexture;
    }

    @Override
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

    @Override
    public void initializeModel(Set<TextureAsset> textureList) {
        for (String state : this.statementModels.keySet()) {
            for (BlockModelComponent component : this.statementModels.get(state)) {
                component.initializeModel(textureList);
            }
        }
    }

    public String getParticleTexture() {
        if (Objects.equals(this.particleTex, "cubecraft:_EMPTY_")) {
            return null;
        }

        return "/asset/%s/texture%s".formatted(this.particleTex.split(":")[0], this.particleTex.split(":")[1]);
    }
}
