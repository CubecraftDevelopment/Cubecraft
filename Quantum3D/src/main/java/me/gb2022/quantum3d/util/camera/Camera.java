package me.gb2022.quantum3d.util.camera;

import org.joml.Matrix4f;
import org.joml.Quaternionf;

public abstract class Camera {
    private final Matrix4f globalViewMatrix = new Matrix4f();
    private final Matrix4f localViewMatrix = new Matrix4f();
    private final Matrix4f projectionMatrix = new Matrix4f().zero();

    private float lastXRot, lastYRot, lastZRot;
    private float xRot, yRot, zRot;
    private double lastX, lastY, lastZ;
    private double x, y, z;

    public void updateMatrix() {
        this.projectionMatrix.set(createProjectionMatrix());

        this.globalViewMatrix.identity();
        var rotAspect = new Quaternionf();

        rotAspect.fromAxisAngleDeg(this.xRot / 180f, this.yRot / 180f, this.zRot / 180f, 180);
        rotAspect.get(this.globalViewMatrix);

        this.localViewMatrix.set(this.globalViewMatrix);
        this.globalViewMatrix.translate((float) -this.x, (float) -this.y, (float) -this.z);
    }

    public void setPosition(double x, double y, double z) {
        this.lastX = this.x;
        this.lastY = this.y;
        this.lastZ = this.z;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void setRotation(float xRot, float yRot, float zRot) {
        this.lastXRot = this.xRot;
        this.lastYRot = this.yRot;
        this.lastZRot = this.zRot;
        this.xRot = xRot;
        this.yRot = yRot;
        this.zRot = zRot;
    }


    //----[matrix]----
    public abstract Matrix4f createProjectionMatrix();

    public Matrix4f getGlobalViewMatrix() {
        return globalViewMatrix;
    }

    public Matrix4f getLocalViewMatrix() {
        return localViewMatrix;
    }

    public Matrix4f getProjectionMatrix() {
        return projectionMatrix;
    }

    public Matrix4f getObjectMatrix(double ox, double oy, double oz) {
        return new Matrix4f(this.localViewMatrix).translate(this.xRot, this.yRot, this.zRot);
    }


    //----[matrix]----
    public double getLastX() {
        return lastX;
    }

    public double getLastY() {
        return lastY;
    }

    public double getLastZ() {
        return lastZ;
    }

    public float getLastXRot() {
        return lastXRot;
    }

    public float getLastYRot() {
        return lastYRot;
    }

    public float getLastZRot() {
        return lastZRot;
    }
}
