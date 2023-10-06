package ink.flybird.cubecraft.client.resource;

import ink.flybird.cubecraft.resource.item.JsonResource;

public class ModelAsset extends JsonResource {
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
