package net.cubecraft.client.resource;


import net.cubecraft.resource.item.ImageResource;
import me.gb2022.quantum3d.texture.ITextureImage;

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
