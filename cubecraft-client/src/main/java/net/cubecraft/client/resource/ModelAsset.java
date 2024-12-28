package net.cubecraft.client.resource;

import net.cubecraft.resource.item.JsonResource;

public final class ModelAsset extends JsonResource {
    public ModelAsset(String namespace, String relativePath) {
        super(namespace, relativePath);
    }

    public ModelAsset(String all) {
        super(all);
    }

    @Override
    public String formatPath(String namespace, String relPath) {
        return "/asset/" + namespace + "/model" + relPath;
    }
}
