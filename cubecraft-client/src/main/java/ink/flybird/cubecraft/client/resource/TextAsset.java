package ink.flybird.cubecraft.client.resource;


import ink.flybird.cubecraft.resource.item.ImageResource;
import ink.flybird.quantum3d_legacy.textures.ITextureImage;

import java.awt.image.BufferedImage;

public final class TextAsset extends ImageResource implements ITextureImage {
    public TextAsset(String namespace, String relativePath) {
        super(namespace, relativePath);
    }

    public TextAsset(String s) {
        super(s);
    }

    @Override
    public String formatPath(String namespace, String relPath) {
        return "/asset/"+namespace+"/text/"+relPath;
    }

    @Override
    public BufferedImage getAsImage() {
        return this.getImage();
    }

    @Override
    public String getName() {
        return this.getAbsolutePath();
    }
}
