package ink.flybird.cubecraft.client.resource;

import ink.flybird.cubecraft.resource.item.XmlResource;

public class UIAsset extends XmlResource {
    public UIAsset(String namespace, String relativePath) {
        super(namespace, relativePath);
    }

    public UIAsset(String all) {
        super(all);
    }

    @Override
    public String formatPath(String namespace, String relPath) {
        return "/asset/" + namespace + "/ui" + relPath;
    }
}
