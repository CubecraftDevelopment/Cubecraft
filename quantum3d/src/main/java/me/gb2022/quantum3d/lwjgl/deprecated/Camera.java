package me.gb2022.quantum3d.lwjgl.deprecated;

import ink.flybird.fcommon.math.AABB;
import me.gb2022.quantum3d.lwjgl.deprecated.GLUtil;
import me.gb2022.quantum3d.lwjgl.deprecated.culling.FrustumCuller;
import me.gb2022.quantum3d.lwjgl.device.GLFWWindow;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3d;
import org.lwjgl.opengl.GL11;

public class Camera {
    private final Vector3d position = new Vector3d();
    private final Vector3d lastPosition = new Vector3d();


    private final Vector3d rotation = new Vector3d();
    private final Vector3d relativePosition = new Vector3d();
    private final FrustumCuller frustum = new FrustumCuller(this);
    private float fov = 70.0f;
    private Matrix4f proj = new Matrix4f();
    private long playerGridX, playerGridY, playerGridZ;
    private double lastRotX, lastRotY, lastRotZ;

    public void setUpGlobalCamera(GLFWWindow window) {
        GLUtil.setupPerspectiveCamera((float) Math.toRadians(this.fov), window.getWidth(), window.getHeight());
        //GL11.glLoadIdentity();
        //GL11.glOrtho(0,window.getWidth()*16f,0,window.getHeight()*16f,,-1000000,1000000);
        //GL11.glLoadIdentity();
        //GL11.glTranslated(window.getWidth()/4f,window.getHeight()/4f,0);
        //GL11.glScaled(10,10,10);

        GL11.glTranslated(this.relativePosition.x, this.relativePosition.y, this.relativePosition.z);
        GL11.glRotated(this.rotation.x, 1, 0, 0);
        GL11.glRotated(this.rotation.y, 0, 1, 0);
        GL11.glRotated(this.rotation.z, 0, 0, 1);

        this.proj = new Matrix4f();
        this.proj.rotate(new Quaternionf(this.rotation.x, this.rotation.y, this.rotation.z, 1.0f));
        this.proj.mul(new Matrix4f().perspective((float) Math.toRadians(this.fov), window.getAspect(), 0, 114514));
        GLUtil.checkError("camera");
    }

    public void setupObjectCamera(Vector3d objPosition) {
        GL11.glTranslated(objPosition.x - this.position.x, objPosition.y - this.position.y, objPosition.z - this.position.z);
    }

    public boolean objectDistanceSmallerThan(Vector3d objPosition, double dist) {
        dist += 4;
        double xd = Math.abs(this.position.x - objPosition.x);
        double yd = Math.abs(this.position.y - objPosition.y);
        double zd = Math.abs(this.position.z - objPosition.z);
        return xd <= dist && yd <= dist && zd <= dist;
    }

    public Vector3d getPosition() {
        return this.position;
    }

    public Vector3d getRelativePosition() {
        return this.relativePosition;
    }

    public Vector3d getRotation() {
        return this.rotation;
    }

    public void setPos(double x, double y, double z) {
        this.lastPosition.set(this.position);
        this.position.set(x, y, z);
    }

    public void setPosRelative(double x, double y, double z) {
        this.relativePosition.set(x, y, z);
    }

    public void setupRotation(double x, double y, double z) {
        this.rotation.set(x, y, z);
    }

    public Matrix4f getCurrentMatrix() {
        return this.proj;
    }

    public void updateRotation() {
        this.lastRotX = this.getRotation().x;
        this.lastRotY = this.getRotation().y;
        this.lastRotZ = this.getRotation().z;
    }

    public boolean isPositionChanged() {
        return (long) (this.getPosition().x / 8) != this.playerGridX ||
                (long) (this.getPosition().y / 8) != this.playerGridY ||
                (long) (this.getPosition().z / 8) != this.playerGridZ;

    }

    public boolean isRotationChanged() {
        return (this.getRotation().x) != this.lastRotX ||
                (this.getRotation().y) != this.lastRotY ||
                (this.getRotation().z) != this.lastRotZ;
    }

    public void setupGlobalTranslate() {
        GL11.glTranslated(-this.getPosition().x, -this.getPosition().y, -this.getPosition().z);
    }

    public AABB castAABB(AABB aabb) {
        AABB aabb2 = new AABB(aabb);
        aabb2.move(-this.position.x, -this.position.y, -this.position.z);
        return aabb2;
    }

    public void setFov(float fov) {
        this.fov = fov;
    }

    public boolean aabbInFrustum(AABB aabb) {
        return this.frustum.aabbVisible(this.castAABB(aabb));
    }

    public void updateFrustum() {
        this.frustum.calculateFrustum();
    }

    public Vector3d getLastPosition() {
        return this.lastPosition;
    }
}

