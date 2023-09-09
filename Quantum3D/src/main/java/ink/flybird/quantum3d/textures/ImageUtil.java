package ink.flybird.quantum3d.textures;

import ink.flybird.quantum3d.BufferAllocation;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;

public class ImageUtil {

    public static ByteBuffer getByteFromBufferedImage_RGBA(BufferedImage img){
        int w=img.getWidth();
        int h=img.getHeight();
        ByteBuffer pixels = BufferAllocation.allocByteBuffer(w*h*4);
        pixels.put(unpackIntAsRGBAByte(readImageAsRawInt(img)));
        pixels.position(0);
        return pixels;
    }

    /**
     * read a buffered image,get pixel data as int array.
     * @param img img
     * @return int array
     */
    public static int[] readImageAsRawInt(BufferedImage img){
        int w=img.getWidth();
        int h=img.getHeight();
        int[] rawPixels = new int[w * h];
        img.getRGB(0, 0, w, h, rawPixels, 0, w);
        return rawPixels;
    }

    /**
     * read all int pixels,unpack them into r8_g8_b8_a8 bytes
     * @param rawPixels raw data
     * @return byte data
     */
    public static byte[] unpackIntAsRGBAByte(int[] rawPixels){
        byte[] newPixels1 = new byte[rawPixels.length*4];
        for (int i = 0; i < rawPixels.length; ++i) {
            int a = rawPixels[i] >> 24 & 0xFF;
            int r = rawPixels[i] >> 16 & 0xFF;
            int g = rawPixels[i] >> 8 & 0xFF;
            int b = rawPixels[i] & 0xFF;
            newPixels1[i * 4] = (byte)r;
            newPixels1[i * 4 + 1] = (byte)g;
            newPixels1[i * 4 + 2] = (byte)b;
            newPixels1[i * 4 + 3] = (byte)a;
        }
        return newPixels1;
    }

    /**
     * read all int pixels,unpack them into a8_r8_g8_b8 bytes
     * @param rawPixels raw data
     * @return byte data
     */
    public static byte[] unpackIntAsARGBByte(int[] rawPixels){
        byte[] newPixels1 = new byte[rawPixels.length*4];
        for (int i = 0; i < rawPixels.length; ++i) {
            int a = rawPixels[i] >> 24 & 0xFF;
            int r = rawPixels[i] >> 16 & 0xFF;
            int g = rawPixels[i] >> 8 & 0xFF;
            int b = rawPixels[i] & 0xFF;
            newPixels1[i * 4] = (byte)a;
            newPixels1[i * 4 + 1] = (byte)r;
            newPixels1[i * 4 + 2] = (byte)g;
            newPixels1[i * 4 + 3] = (byte)b;
        }
        return newPixels1;
    }
}
