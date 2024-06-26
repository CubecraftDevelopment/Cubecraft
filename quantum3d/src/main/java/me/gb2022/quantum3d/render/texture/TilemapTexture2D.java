package me.gb2022.quantum3d.render.texture;

import me.gb2022.commons.threading.TaskProgressCallback;
import me.gb2022.quantum3d.ITextureImage;

import java.awt.image.BufferedImage;

public interface TilemapTexture2D extends Texture2D {

    void addTexture(ITextureImage image);

    float exactTextureU(String image, double u);

    float exactTextureV(String image, double v);

    void register(ITextureImage file);

    void loadWithProgressUpdate(TaskProgressCallback l);

    BufferedImage export();

    void upload();
}
