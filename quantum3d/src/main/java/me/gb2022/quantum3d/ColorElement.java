package me.gb2022.quantum3d;

import ink.flybird.fcommon.math.MathHelper;

import java.nio.FloatBuffer;

public class ColorElement {
    private final float red;
    private final float green;
    private final float blue;
    private final float alpha;

    private ColorElement(float red, float green, float blue, float alpha) {
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.alpha = alpha;
    }

    public static ColorElement RGBA(int color) {
        byte red = (byte) (color >> 24);
        byte green = (byte) (color >> 16);
        byte blue = (byte) (color >> 8);
        byte alpha = (byte) (color);
        return RGBA(red, green, blue, alpha);
    }

    public static ColorElement ARGB(int color) {
        byte alpha = (byte) (color >> 24);
        byte red = (byte) (color >> 16);
        byte green = (byte) (color >> 8);
        byte blue = (byte) (color);
        return RGBA(red, green, blue, alpha);
    }

    public static ColorElement RGBA(int red, int green, int blue, int alpha) {
        float r2 = (float) (red & 255) / 255.0F;
        float g2 = (float) (green & 255) / 255.0F;
        float b2 = (float) (blue & 255) / 255.0F;
        float a2 = (float) (alpha & 255) / 255.0F;
        return RGBA(r2, g2, b2, a2);
    }

    public static ColorElement ARGB(int alpha, int red, int green, int blue) {
        return RGBA(red, green, blue, alpha);
    }

    public static ColorElement RGBA(float red, float green, float blue, float alpha) {
        return new ColorElement(red, green, blue, alpha);
    }

    public static ColorElement ARGB(float alpha, float red, float green, float blue) {
        return new ColorElement(red, green, blue, alpha);
    }

    public static ColorElement RGB(float red, float green, float blue) {
        return RGBA(red, green, blue, 1.0f);
    }

    public static ColorElement RGB(int red, int green, int blue) {
        return RGBA(red, green, blue, 255);
    }

    public static ColorElement RGB(int color) {
        byte red = (byte) (color >> 16);
        byte green = (byte) (color >> 8);
        byte blue = (byte) color;
        return RGBA(red, green, blue, 255);
    }

    public float getRed() {
        return red;
    }

    public float getGreen() {
        return green;
    }

    public float getBlue() {
        return blue;
    }

    public float getAlpha() {
        return alpha;
    }

    public int getRedB() {
        return (int) (red * 255f);
    }

    public int getGreenB() {
        return (int) (green * 255f);
    }

    public int getBlueB() {
        return (int) (blue * 255f);
    }

    public int getAlphaB() {
        return (int) (alpha * 255f);
    }

    public void toFloatRGBA(FloatBuffer buffer) {
        buffer.put(this.getRed())
                .put(this.getGreen())
                .put(this.getBlue())
                .put(this.getAlpha());
    }

    public void toFloatARGB(FloatBuffer buffer) {
        buffer.put(this.getAlpha())
                .put(this.getRed())
                .put(this.getGreen())
                .put(this.getBlue());

    }

    public static ColorElement parseFromString(String s) {
        if (s.startsWith("#") || s.startsWith("0x") || s.startsWith("0X")) {
            return RGB(MathHelper.hex2Int(s.replace("#", "")
                    .replace("0x", "")
                    .replace("0X", "")));
        }
        return RGB(Integer.parseInt(s));
    }

    public double[] RGBA_F() {
        return new double[]{this.red,this.green,this.blue,this.alpha};
    }
    public double[] RGB_F() {
        return new double[]{this.red,this.green,this.blue};
    }
}
