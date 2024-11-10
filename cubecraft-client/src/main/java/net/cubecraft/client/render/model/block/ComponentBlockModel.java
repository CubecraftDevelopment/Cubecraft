package net.cubecraft.client.render.model.block;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.gb2022.commons.registry.TypeItem;
import me.gb2022.quantum3d.render.vertex.VertexBuilder;
import net.cubecraft.client.render.chunk.container.ChunkLayerContainerFactory;
import net.cubecraft.client.render.model.block.component.BlockModelComponent;
import net.cubecraft.client.render.model.block.component.Cube;
import net.cubecraft.client.resource.TextureAsset;
import net.cubecraft.client.util.DeserializedConstructor;
import net.cubecraft.resource.MultiAssetContainer;
import net.cubecraft.util.register.Registered;
import net.cubecraft.world.BlockAccessor;
import net.cubecraft.world.block.access.BlockAccess;

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
                component.add(new Cube(comp.get(i).getAsJsonObject()));
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
    public void render(BlockAccess block, BlockAccessor accessor, Registered<ChunkLayerContainerFactory.Provider> layer, int face, float x, float y, float z, VertexBuilder builder) {
        Set<String> set = this.statementModels.keySet();

        for (String s : set) {
            if (!block.getBlock().queryBoolean(s, block)) {
                //continue;
            }
            ArrayList<BlockModelComponent> components;
            components = this.statementModels.get(s);
            if (components == null) {
                components = this.statementModels.get("default");
            }
            for (BlockModelComponent component : components) {
                component.render(builder, layer, face, accessor, block, x, y, z);
            }
        }
    }

    @Override
    public void provideTileMapItems(MultiAssetContainer<TextureAsset> list) {
        for (String state : this.statementModels.keySet()) {
            for (BlockModelComponent component : this.statementModels.get(state)) {
                component.provideTileMapItems(list);
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
