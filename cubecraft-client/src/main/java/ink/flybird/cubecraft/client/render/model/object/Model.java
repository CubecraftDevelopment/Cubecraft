package ink.flybird.cubecraft.client.render.model.object;

import ink.flybird.cubecraft.client.resource.TextureAsset;

import java.util.Set;

public interface Model {
    void initializeModel(Set<TextureAsset> textureList);
}
