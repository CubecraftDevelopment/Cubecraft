package me.gb2022.quantum3d.util.camera;

import org.joml.Matrix4f;

public final class GUICamera extends Camera {
    private int screenWidth, screenHeight;

    @Override
    public Matrix4f createProjectionMatrix() {
        return new Matrix4f().ortho(0, this.screenWidth, 0, this.screenHeight, -100, 100);
    }

    public void resize(int width, int height) {
        this.screenWidth = width;
        this.screenHeight = height;
    }

    public int getScreenWidth() {
        return screenWidth;
    }

    public int getScreenHeight() {
        return screenHeight;
    }
}
