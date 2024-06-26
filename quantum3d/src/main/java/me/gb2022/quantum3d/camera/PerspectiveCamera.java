package me.gb2022.quantum3d.camera;

import me.gb2022.commons.math.AABB;
import me.gb2022.quantum3d.FixedJOMLFrustum;
import org.joml.Matrix4f;

public final class PerspectiveCamera extends ICamera {
    private final FixedJOMLFrustum frustum = new FixedJOMLFrustum();
    private float fov = 70.0f;

    private float nearClipDistance = 0.0f;
    private float farClipDistance = 1024.0f;

    public float getFarClipDistance() {
        return farClipDistance;
    }

    public void setFarClipDistance(float farClipDistance) {
        this.farClipDistance = farClipDistance;
    }

    public float getNearClipDistance() {
        return nearClipDistance;
    }

    public void setNearClipDistance(float nearClipDistance) {
        this.nearClipDistance = nearClipDistance;
    }

    public float getFov() {
        return fov;
    }

    public void setFov(float fov) {
        this.fov = fov;
    }

    @Override
    public Matrix4f getMVPMatrix() {
        Matrix4f mat = new Matrix4f();
        float aspect = this.getViewport().z() / (float) this.getViewport().w();
        mat.perspective(this.fov, aspect, this.nearClipDistance, this.farClipDistance);
        return mat;
    }

    @Override
    public boolean visible(AABB aabb) {
        AABB aabb2 = this.castAABB(aabb);
        return frustum.testAab(aabb2.minPos(), aabb.maxPos());
    }
}
