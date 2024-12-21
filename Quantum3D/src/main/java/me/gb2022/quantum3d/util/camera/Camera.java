package me.gb2022.quantum3d.util.camera;

import org.joml.Matrix4f;
import org.joml.Vector3d;
import org.joml.Vector3f;

@SuppressWarnings("unchecked")
public abstract class Camera<I extends Camera<?>> {
    private final Vector3f relativePosition = new Vector3f(0, 0, 0);

    private final PoseStack poseStack = new PoseStack();
    private final Matrix4f projection = new Matrix4f();
    private float lastYaw, lastPitch, lastRoll;
    private float yaw, pitch, roll;
    private double lastX, lastY, lastZ;
    private double x, y, z;

    public abstract Matrix4f createProjectionMatrix();

    public I local() {
        this.projection.set(createProjectionMatrix());
        MatrixAppender.setProjectionMatrix(this.createProjectionMatrix());

        this.poseStack.translate(this.relativePosition);

        this.poseStack.rotate(this.yaw, 1, 0, 0);
        this.poseStack.rotate(this.pitch, 0, 1, 0);
        this.poseStack.rotate(this.roll, 0, 0, 1);

        return (I) this;
    }

    public I global() {
        this.poseStack.translate((float) -this.x, (float) -this.y, (float) -this.z);

        return (I) this;
    }

    public I object(Vector3d objPosition) {
        var rx = (float) (objPosition.x - this.x);
        var ry = (float) (objPosition.y - this.y);
        var rz = (float) (objPosition.z - this.z);

        this.poseStack.translate(rx, ry, rz);
        return (I) this;
    }

    public I object(double x, double y, double z) {
        var rx = (float) (x - this.x);
        var ry = (float) (y - this.y);
        var rz = (float) (z - this.z);

        this.poseStack.translate(rx, ry, rz);
        return (I) this;
    }

    public I push() {
        this.poseStack.pushMatrix();
        return (I) this;
    }

    public void set() {
        MatrixAppender.setModelViewMatrix(this.poseStack.getMatrix());
    }

    public void pop() {
        this.poseStack.popMatrix();
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
        this.lastYaw = this.yaw;
        this.lastPitch = this.pitch;
        this.lastRoll = this.roll;
        this.yaw = xRot;
        this.pitch = yRot;
        this.roll = zRot;
    }

    public void setRelativePosition(float x, float y, float z) {
        this.relativePosition.set(x, y, z);
    }

    public boolean isRotationChanged() {
        return this.yaw != this.lastYaw || this.pitch != this.lastPitch || this.roll != this.lastRoll;
    }


    //----[access]----
    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public float getYaw() {
        return yaw;
    }

    public float getPitch() {
        return pitch;
    }

    public float getRoll() {
        return roll;
    }

    public double getLastX() {
        return lastX;
    }

    public double getLastY() {
        return lastY;
    }

    public double getLastZ() {
        return lastZ;
    }

    public float getLastYaw() {
        return lastYaw;
    }

    public float getLastPitch() {
        return lastPitch;
    }

    public float getLastRoll() {
        return lastRoll;
    }

    public PoseStack getPoseStack() {
        return poseStack;
    }

    public Matrix4f getProjection() {
        return projection;
    }
}
