package me.gb2022.quantum3d.camera;

import me.gb2022.commons.math.AABB;
import me.gb2022.quantum3d.render.RenderContext;
import org.joml.Math;
import org.joml.*;

public abstract class ICamera {
    private final Vector4i viewport = new Vector4i(0, 0, 1280, 720);

    private final Vector3d position = new Vector3d();
    private final Vector3f rotation = new Vector3f();
    private final Vector3f relativePosition = new Vector3f();

    public abstract Matrix4f getMVPMatrix();

    private void attachGlobalTransform(Matrix4f mat) {
        mat.rotate(new AxisAngle4f(Math.toRadians(this.rotation.x), 1, 0, 0));
        mat.rotate(new AxisAngle4f(Math.toRadians(this.rotation.y), 0, 1, 0));
        mat.rotate(new AxisAngle4f(Math.toRadians(this.rotation.z), 0, 0, 1));
    }

    public final Matrix4f getRelativeCamera() {
        Matrix4f mat = getMVPMatrix();
        this.attachGlobalTransform(mat);
        return mat;
    }

    public final Matrix4f getGlobalCamera() {
        Matrix4f mat = this.getRelativeCamera();

        float x = ((float) -this.position.x);
        float y = ((float) -this.position.z);
        float z = ((float) -this.position.z);

        mat.transform(new Vector4f(x, y, z, 0f));
        return mat;
    }

    public final Matrix4f getObjectCamera(Vector3d objectPosition) {
        Matrix4f mat = this.getRelativeCamera();

        float x = (float) (objectPosition.x - this.position.x);
        float y = (float) (objectPosition.y - this.position.y);
        float z = (float) (objectPosition.z - this.position.z);

        mat.transform(new Vector4f(x, y, z, 0f));
        return mat;
    }

    public final void setCameraViewport(RenderContext context) {
        context.setViewport(this.viewport.x(), this.viewport.y(), this.viewport.z(), this.viewport.w());
    }


    public final Vector3d getPosition() {
        return this.position;
    }

    public final Vector3f getRelativePosition() {
        return this.relativePosition;
    }

    public final Vector3f getRotation() {
        return this.rotation;
    }

    public final Vector4i getViewport() {
        return viewport;
    }


    public final void setPosition(double x, double y, double z) {
        this.position.set(this.position);
        this.position.set(x, y, z);
    }

    public final void setRelativePosition(double x, double y, double z) {
        this.relativePosition.set(x, y, z);
    }

    public final void setRotation(double x, double y, double z) {
        this.rotation.set(x, y, z);
    }

    public final void setViewport(int x, int y, int width, int height) {
        this.viewport.set(x, y, width, height);
    }


    public AABB castAABB(AABB aabb) {
        AABB aabb2 = new AABB(aabb);
        aabb2.move(-this.position.x, -this.position.y, -this.position.z);
        return aabb2;
    }

    public boolean visible(AABB aabb) {
        return true;
    }
}
