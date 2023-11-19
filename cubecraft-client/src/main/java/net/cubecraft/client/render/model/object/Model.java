package net.cubecraft.client.render.model.object;

import net.cubecraft.client.resource.TextureAsset;

import java.util.Set;

public interface Model {
    void initializeModel(Set<TextureAsset> textureList);
}
