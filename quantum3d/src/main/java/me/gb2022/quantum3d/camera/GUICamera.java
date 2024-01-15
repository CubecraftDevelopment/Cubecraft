package me.gb2022.quantum3d.camera;

import ink.flybird.fcommon.math.AABB;
import org.joml.Matrix4f;
import org.joml.Vector4i;

public class GUICamera extends ICamera {
    @Override
    public Matrix4f getMVPMatrix() {
        Vector4i bound = this.getViewport();
        return new Matrix4f().ortho(bound.x(), bound.y(), bound.w(), bound.z(), -1000, 1000);
    }

    @Override
    public boolean visible(AABB aabb) {
        Vector4i bound = this.getViewport();

        int x0 = bound.x();
        int y0 = bound.y();
        int x1 = x0 + bound.z();
        int y1 = y0 + bound.w();

        double xMin = aabb.x0;
        double yMin = aabb.y1;
        double xMax = aabb.x1;
        double yMax = aabb.y1;

        return (xMax >= x0 && yMax >= y0) && (xMin <= x1 && yMin <= y1);
    }
}
