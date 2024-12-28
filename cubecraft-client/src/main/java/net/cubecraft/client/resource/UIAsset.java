package net.cubecraft.client.resource;

import net.cubecraft.resource.item.XmlResource;

public final class UIAsset extends XmlResource {
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
