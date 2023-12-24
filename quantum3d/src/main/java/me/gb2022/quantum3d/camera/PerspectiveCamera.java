package me.gb2022.quantum3d.camera;

import me.gb2022.quantum3d.render.RenderContext;
import org.joml.Matrix4d;

public final class PerspectiveCamera extends ICamera {
    private float fov = 70.0f;
    private float zNear = 0;
    private float zFar = 1000;

    public PerspectiveCamera(RenderContext context) {
        super(context);
    }

    @Override
    public Matrix4d getMVPMatrix() {
        Matrix4d mat = new Matrix4d();
        mat.perspective(this.fov, this.getViewport().z() / (float) this.getViewport().w(), this.zNear, this.zFar);
        return mat;
    }

    public float getFov() {
        return fov;
    }

    public void setFov(float fov) {
        this.fov = fov;
    }

    public float getzNear() {
        return zNear;
    }

    public void setzNear(float zNear) {
        this.zNear = zNear;
    }

    public float getzFar() {
        return zFar;
    }

    public void setzFar(float zFar) {
        this.zFar = zFar;
    }
}
