package me.gb2022.quantum3d.util.camera;

import org.joml.Matrix4f;

public final class PerspectiveCamera extends Camera<PerspectiveCamera> {
    private float fov = 70.0f;
    private float aspect = 1.0f;

    public PerspectiveCamera() {
    }

    public PerspectiveCamera(final float fov) {
        this.fov = fov;
    }

    public PerspectiveCamera(float fov, float aspect) {
        this.fov = fov;
        this.aspect = aspect;
    }

    @Override
    public Matrix4f createProjectionMatrix() {
        var proj = new Matrix4f();
        proj = proj.perspective((float) Math.toRadians(this.fov), this.aspect, 0.03f, 131072f);

        return proj;
    }

    public void setAspectRatio(int vpWidth, int vpHeight) {
        this.aspect = (float) vpWidth / vpHeight;
    }

    public void setAspectRatio(float a) {
        this.aspect = a;
    }

    public void setFov(float fov) {
        this.fov = fov;
    }
}
