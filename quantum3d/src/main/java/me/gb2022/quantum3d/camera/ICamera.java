package me.gb2022.quantum3d.camera;

import ink.flybird.fcommon.math.AABB;
import me.gb2022.quantum3d.FixedJOMLFrustum;
import me.gb2022.quantum3d.render.RenderContext;
import org.joml.Matrix4d;
import org.joml.Matrix4dStack;
import org.joml.Vector3d;
import org.joml.Vector4i;

import java.util.concurrent.atomic.AtomicInteger;

public abstract class ICamera {
    private final RenderContext context;
    private final Vector3d position = new Vector3d();
    private final Vector3d rotation = new Vector3d();
    private final Vector3d relativePosition = new Vector3d();
    private final Vector4i viewport = new Vector4i(0, 0, 1280, 720);
    private final AtomicInteger layer = new AtomicInteger();
    private final Matrix4dStack matrix = new Matrix4dStack(16);
    private final FixedJOMLFrustum frustum = new FixedJOMLFrustum();

    protected ICamera(RenderContext context) {
        this.context = context;
        this.matrix.set(new Matrix4d().identity());
    }

    public abstract Matrix4d getMVPMatrix();


    public void setGlobalCamera() {
        //this.matrix.pushMatrix();
        Matrix4d mat = this.getMVPMatrix();
        /*
        mat.translate(
                -this.position.x(),
                -this.position.y(),
                -this.position.z()
        );
        mat.rotate(new Quaterniond(
                this.rotation.x(),
                this.rotation.y(),
                this.rotation.z(),
                1.0
        ));
        mat.translate(
                -this.relativePosition.x(),
                -this.relativePosition.y(),
                -this.relativePosition.z()
        );
         */


        this.context.setViewport(
                this.viewport.x(),
                this.viewport.y(),
                this.viewport.z(),
                this.viewport.w());
        this.context.setMatrix(mat);
        this.layer.incrementAndGet();
    }


    public void pushObjectCamera(Vector3d objectPosition) {
        this.matrix.pushMatrix();
        this.matrix.translate(objectPosition);
        this.setMatrix(this.matrix);
        this.layer.incrementAndGet();
    }

    public void popObjectCamera() {
        this.matrix.popMatrix();
        this.setMatrix(this.matrix);
        this.layer.decrementAndGet();
    }

    public void popGlobalCamera() {
        this.matrix.popMatrix();
        this.setMatrix(this.matrix);
        this.layer.decrementAndGet();
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

    public Vector4i getViewport() {
        return viewport;
    }


    public void setPosition(double x, double y, double z) {
        this.position.set(this.position);
        this.position.set(x, y, z);
    }

    public void setRelativePosition(double x, double y, double z) {
        this.relativePosition.set(x, y, z);
    }

    public void setRotation(double x, double y, double z) {
        this.rotation.set(x, y, z);
    }

    public void setViewport(int x, int y, int width, int height) {
        this.viewport.set(x, y, width, height);
    }

    public Matrix4dStack getMatrix() {
        return this.matrix;
    }

    public void setMatrix(Matrix4d mat) {
        this.context.setViewport(
                this.viewport.x(),
                this.viewport.y(),
                this.viewport.z(),
                this.viewport.w()
        );
        this.context.setMatrix(mat);
        this.matrix.set(mat);
        this.frustum.set(mat);
    }

    public AtomicInteger getLayer() {
        return this.layer;
    }

    public RenderContext getContext() {
        return this.context;
    }

    public FixedJOMLFrustum getFrustum() {
        return frustum;
    }


    public boolean visible(AABB aabb) {
        return this.frustum.testAab(aabb.minPos(), null);//fixme:aabb.maxPos()
    }

    public void refresh() {
        this.matrix.clear();
        this.setMatrix(new Matrix4d().identity());
        this.context.setMatrix(new Matrix4d().identity());
    }
}
