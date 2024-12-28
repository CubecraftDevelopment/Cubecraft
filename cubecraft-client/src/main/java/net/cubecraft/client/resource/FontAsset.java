package net.cubecraft.client.resource;

import net.cubecraft.resource.item.IResource;

import java.awt.*;
import java.io.InputStream;

public final class FontAsset extends IResource {
    private Font font;

    public FontAsset(String namespace, String relativePath) {
        super(namespace, relativePath);
    }

    public FontAsset(String all) {
        super(all);
    }

    @Override
    public void load(InputStream stream) throws Exception {
        this.font = Font.createFont(Font.TRUETYPE_FONT, stream);
    }

    @Override
    public String formatPath(String namespace, String relPath) {
        return "/asset/" + namespace + "/font" + relPath;
    }

    public Font getFont() {
        return this.font;
    }

    public Font getSubFont(int style) {
        return this.font.deriveFont(style);
    }
}
