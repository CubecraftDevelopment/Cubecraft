package net.cubecraft.client.render.model;

import net.cubecraft.SharedContext;
import net.cubecraft.client.ClientSharedContext;
import net.cubecraft.client.render.model.object.Model;
import net.cubecraft.client.resource.ModelAsset;

import java.util.HashMap;

public final class ModelManager<I extends Model> {
    private final Class<I> clazz;
    private final ModelAsset fallback;
    private final HashMap<String, I> models = new HashMap<>();

    public ModelManager(Class<I> clazz, ModelAsset fallback) {
        this.clazz = clazz;
        this.fallback = fallback;
        if(fallback==null){
            return;
        }
        ClientSharedContext.RESOURCE_MANAGER.loadResource(this.fallback);
    }

    public I get(String id) {
        return models.get(id);
    }

    public void load(ModelAsset asset) {
        String json = asset.getRawText();
        String file = asset.getAbsolutePath();
        if (json == null) {
            json = this.fallback.getRawText();
        }
        if (json == null) {
            throw new RuntimeException("failed to fetch model text:" + file);
        }

        I model = SharedContext.createJsonReader().fromJson(json, this.clazz);
        this.models.put(file, model);
    }
}
