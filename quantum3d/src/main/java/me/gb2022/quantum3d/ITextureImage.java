package me.gb2022.quantum3d;

import java.awt.image.BufferedImage;

public interface ITextureImage {
    byte[] getPixels();

    BufferedImage getImage();

    int getWidth();

    int getHeight();

    String getName();
}