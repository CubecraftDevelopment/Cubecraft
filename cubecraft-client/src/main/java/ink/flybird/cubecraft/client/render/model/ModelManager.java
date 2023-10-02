package ink.flybird.cubecraft.client.render.model;

import ink.flybird.cubecraft.client.ClientSharedContext;
import ink.flybird.cubecraft.client.render.model.object.Model;
import ink.flybird.cubecraft.resource.Resource;
import ink.flybird.cubecraft.resource.ResourceLocation;
import ink.flybird.fcommon.logging.Logger;
import ink.flybird.fcommon.logging.SimpleLogger;
import ink.flybird.cubecraft.SharedContext;

import java.util.HashMap;

public class ModelManager<I extends Model> {
    private final Class<I> clazz;
    private final ResourceLocation fallback;
    private final Logger logger = new SimpleLogger("ModelManager");
    private final HashMap<String, I> models = new HashMap<>();

    public ModelManager(Class<I> clazz, ResourceLocation fallback) {
        this.clazz = clazz;
        this.fallback = fallback;
    }

    public I get(String id) {
        return models.get(id);
    }

    public void load(String file) {
        //TextResource resource = new TextResource(file);
        //ClientSharedContext.RESOURCE_MANAGER.registerResource("default", file, resource);

        Resource res;
        try {
            res = ClientSharedContext.RESOURCE_MANAGER.getResource(file);
        } catch (Exception e) {
            res = ClientSharedContext.RESOURCE_MANAGER.getResource(fallback);
        }


        String json = res.getAsText();
        try {
            I model = SharedContext.createJsonReader().fromJson(json, clazz);
            this.models.put(file, model);
        } catch (Exception e) {
            I model = SharedContext.createJsonReader().fromJson(ClientSharedContext.RESOURCE_MANAGER.getResource(fallback).getAsText(), clazz);
            this.models.put(file, model);
            this.logger.exception(e);
        }
    }

    public void load(ResourceLocation loc) {
        load(loc.format());
    }
}
