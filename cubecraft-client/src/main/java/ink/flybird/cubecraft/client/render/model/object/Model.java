package ink.flybird.cubecraft.client.render.model.object;

import ink.flybird.cubecraft.client.resources.resource.ImageResource;

import java.util.Set;

public interface Model {
    void initializeModel(Set<ImageResource> textureList);
}
