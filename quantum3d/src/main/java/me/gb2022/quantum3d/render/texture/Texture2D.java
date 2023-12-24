package me.gb2022.quantum3d.render.texture;

import me.gb2022.quantum3d.ITextureImage;

public interface Texture2D extends Texture {
    int getWidth();

    int getHeight();

    void upload(ITextureImage image);
}