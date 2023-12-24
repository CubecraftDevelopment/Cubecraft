package me.gb2022.quantum3d.camera;

import me.gb2022.quantum3d.render.RenderContext;
import org.joml.Matrix4d;
import org.joml.Vector4i;

public class GUICamera extends ICamera {
    private final Vector4i bound = new Vector4i(0, 0, 1280, 720);

    public GUICamera(RenderContext context) {
        super(context);
    }

    @Override
    public Matrix4d getMVPMatrix() {
        return new Matrix4d().ortho(bound.x(), bound.y(), bound.w(), bound.z(), -1000, 1000);
    }

    @Override
    public void setViewport(int x, int y, int width, int height) {
        super.setViewport(x, y, width, height);
        this.bound.set(x, y, width, height);
    }
}
