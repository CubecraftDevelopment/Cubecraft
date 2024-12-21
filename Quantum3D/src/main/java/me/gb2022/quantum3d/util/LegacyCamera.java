package me.gb2022.quantum3d.util;

import me.gb2022.quantum3d.util.camera.MatrixAppender;
import me.gb2022.quantum3d.util.camera.PoseStack;
import org.joml.Matrix4f;
import org.joml.Vector3d;

public class LegacyCamera {
    private final Vector3d position = new Vector3d();
    private final Vector3d lastPosition = new Vector3d();

    private final Vector3d rotation = new Vector3d();
    private final Vector3d relativePosition = new Vector3d();
    private final PoseStack poseStack = new PoseStack();
    public float fov = 70.0f;
    private Matrix4f proj = new Matrix4f();
    private double lastRotX, lastRotY, lastRotZ;

    public LegacyCamera global() {
        GLUtil.checkError("pre-global-camera");
        GLUtil.setupPerspectiveCamera((float) Math.toRadians(this.fov), 1280, 720);

        //GL11.glLoadIdentity();
        //GL11.glOrtho(0,window.getWindowWidth()*16f,0,window.getWindowHeight()*16f,,-1000000,1000000);
        //GL11.glLoadIdentity();
        //GL11.glTranslated(window.getWindowWidth()/4f,window.getWindowHeight()/4f,0);
        //GL11.glScaled(10,10,10);

        this.poseStack.translate((float) this.relativePosition.x, (float) this.relativePosition.y, (float) this.relativePosition.z);

        this.poseStack.rotate((float) this.rotation.x, 1, 0, 0);
        this.poseStack.rotate((float) this.rotation.y, 0, 1, 0);
        this.poseStack.rotate((float) this.rotation.z, 0, 0, 1);

        //GL11.glRotated(this.rotation.x, 1, 0, 0);
        //GL11.glRotated(this.rotation.y, 0, 1, 0);
        //GL11.glRotated(this.rotation.z, 0, 0, 1);

        GLUtil.checkError("camera");

        return this;
    }



    public LegacyCamera object(Vector3d objPosition) {
        var rx = (float) (objPosition.x - this.position.x);
        var ry = (float) (objPosition.y - this.position.y);
        var rz = (float) (objPosition.z - this.position.z);

        this.poseStack.translate(rx, ry, rz);
        return this;
    }

    public LegacyCamera push() {
        this.poseStack.pushMatrix();
        return this;
    }

    public void set() {
        MatrixAppender.setModelViewMatrix(this.poseStack.getMatrix());
    }

    public Vector3d getPosition() {
        return this.position;
    }

    public Vector3d getRotation() {
        return this.rotation;
    }

    public LegacyCamera object(double x, double y, double z) {
        var rx = (float) (x - this.position.x);
        var ry = (float) (y - this.position.y);
        var rz = (float) (z - this.position.z);

        this.poseStack.translate(rx, ry, rz);
        return this;
    }
}

