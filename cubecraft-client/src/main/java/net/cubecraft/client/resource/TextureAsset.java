package net.cubecraft.client.resource;


import net.cubecraft.resource.item.ImageResource;
import ink.flybird.quantum3d_legacy.textures.ITextureImage;

import java.awt.image.BufferedImage;

public final class TextureAsset extends ImageResource implements ITextureImage {
    public TextureAsset(String namespace, String relativePath) {
        super(namespace, relativePath);
    }

    public TextureAsset(String s) {
        super(s);
    }

    public static String format(String loc) {
        String namespace = loc.trim().split(":")[0];
        String relativePath = loc.trim().split(":")[1];

        return "/asset/" + namespace+"/texture" + relativePath;
    }

    @Override
    public String formatPath(String namespace, String relPath) {
        return format(namespace+":"+relPath);
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
