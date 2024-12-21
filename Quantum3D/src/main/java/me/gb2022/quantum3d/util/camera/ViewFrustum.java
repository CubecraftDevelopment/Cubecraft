package me.gb2022.quantum3d.util.camera;

import me.gb2022.commons.math.AABB;
import org.joml.FrustumIntersection;
import org.joml.Matrix4f;
import org.joml.Vector3d;

public final class ViewFrustum {
    public final float[][] m_Frustum = new float[6][4];
    final float[] proj = new float[16];
    final float[] modl = new float[16];
    final float[] clip = new float[16];
    private final Matrix4f projectionMatrix = new Matrix4f();
    private final Matrix4f viewMatrix = new Matrix4f();
    private final float[] frustum = new float[24];  // 存储6个平面
    private final FrustumIntersection intersection = new FrustumIntersection();

    public ViewFrustum(Matrix4f projection, Matrix4f view) {
        update(projection, view);
    }


    public ViewFrustum() {
    }

    private void normalizePlane(float[][] frustum, int side) {
        float magnitude = (float) Math.sqrt(frustum[side][0] * frustum[side][0] + frustum[side][1] * frustum[side][1] + frustum[side][2] * frustum[side][2]);
        frustum[side][0] /= magnitude;
        frustum[side][1] /= magnitude;
        frustum[side][2] /= magnitude;
        frustum[side][3] /= magnitude;
    }

    public void update(Camera<?> reference) {
        this.update(reference.getProjection(), reference.getPoseStack().getMatrix());
    }

    public void update(Matrix4f projection, Matrix4f view) {
        this.projectionMatrix.set(projection);
        this.viewMatrix.set(view);
        compute();
    }

    private void compute() {
        this.viewMatrix.get(this.modl);
        this.projectionMatrix.get(this.proj);

        this.clip[0] = this.modl[0] * this.proj[0] + this.modl[1] * this.proj[4] + this.modl[2] * this.proj[8] + this.modl[3] * this.proj[12];
        this.clip[1] = this.modl[0] * this.proj[1] + this.modl[1] * this.proj[5] + this.modl[2] * this.proj[9] + this.modl[3] * this.proj[13];
        this.clip[2] = this.modl[0] * this.proj[2] + this.modl[1] * this.proj[6] + this.modl[2] * this.proj[10] + this.modl[3] * this.proj[14];
        this.clip[3] = this.modl[0] * this.proj[3] + this.modl[1] * this.proj[7] + this.modl[2] * this.proj[11] + this.modl[3] * this.proj[15];
        this.clip[4] = this.modl[4] * this.proj[0] + this.modl[5] * this.proj[4] + this.modl[6] * this.proj[8] + this.modl[7] * this.proj[12];
        this.clip[5] = this.modl[4] * this.proj[1] + this.modl[5] * this.proj[5] + this.modl[6] * this.proj[9] + this.modl[7] * this.proj[13];
        this.clip[6] = this.modl[4] * this.proj[2] + this.modl[5] * this.proj[6] + this.modl[6] * this.proj[10] + this.modl[7] * this.proj[14];
        this.clip[7] = this.modl[4] * this.proj[3] + this.modl[5] * this.proj[7] + this.modl[6] * this.proj[11] + this.modl[7] * this.proj[15];
        this.clip[8] = this.modl[8] * this.proj[0] + this.modl[9] * this.proj[4] + this.modl[10] * this.proj[8] + this.modl[11] * this.proj[12];
        this.clip[9] = this.modl[8] * this.proj[1] + this.modl[9] * this.proj[5] + this.modl[10] * this.proj[9] + this.modl[11] * this.proj[13];
        this.clip[10] = this.modl[8] * this.proj[2] + this.modl[9] * this.proj[6] + this.modl[10] * this.proj[10] + this.modl[11] * this.proj[14];
        this.clip[11] = this.modl[8] * this.proj[3] + this.modl[9] * this.proj[7] + this.modl[10] * this.proj[11] + this.modl[11] * this.proj[15];
        this.clip[12] = this.modl[12] * this.proj[0] + this.modl[13] * this.proj[4] + this.modl[14] * this.proj[8] + this.modl[15] * this.proj[12];
        this.clip[13] = this.modl[12] * this.proj[1] + this.modl[13] * this.proj[5] + this.modl[14] * this.proj[9] + this.modl[15] * this.proj[13];
        this.clip[14] = this.modl[12] * this.proj[2] + this.modl[13] * this.proj[6] + this.modl[14] * this.proj[10] + this.modl[15] * this.proj[14];
        this.clip[15] = this.modl[12] * this.proj[3] + this.modl[13] * this.proj[7] + this.modl[14] * this.proj[11] + this.modl[15] * this.proj[15];

        this.m_Frustum[0][0] = this.clip[3] - this.clip[0];
        this.m_Frustum[0][1] = this.clip[7] - this.clip[4];
        this.m_Frustum[0][2] = this.clip[11] - this.clip[8];
        this.m_Frustum[0][3] = this.clip[15] - this.clip[12];
        this.normalizePlane(this.m_Frustum, 0);
        this.m_Frustum[1][0] = this.clip[3] + this.clip[0];
        this.m_Frustum[1][1] = this.clip[7] + this.clip[4];
        this.m_Frustum[1][2] = this.clip[11] + this.clip[8];
        this.m_Frustum[1][3] = this.clip[15] + this.clip[12];
        this.normalizePlane(this.m_Frustum, 1);
        this.m_Frustum[2][0] = this.clip[3] + this.clip[1];
        this.m_Frustum[2][1] = this.clip[7] + this.clip[5];
        this.m_Frustum[2][2] = this.clip[11] + this.clip[9];
        this.m_Frustum[2][3] = this.clip[15] + this.clip[13];
        this.normalizePlane(this.m_Frustum, 2);
        this.m_Frustum[3][0] = this.clip[3] - this.clip[1];
        this.m_Frustum[3][1] = this.clip[7] - this.clip[5];
        this.m_Frustum[3][2] = this.clip[11] - this.clip[9];
        this.m_Frustum[3][3] = this.clip[15] - this.clip[13];
        this.normalizePlane(this.m_Frustum, 3);
        this.m_Frustum[4][0] = this.clip[3] - this.clip[2];
        this.m_Frustum[4][1] = this.clip[7] - this.clip[6];
        this.m_Frustum[4][2] = this.clip[11] - this.clip[10];
        this.m_Frustum[4][3] = this.clip[15] - this.clip[14];
        this.normalizePlane(this.m_Frustum, 4);
        this.m_Frustum[5][0] = this.clip[3] + this.clip[2];
        this.m_Frustum[5][1] = this.clip[7] + this.clip[6];
        this.m_Frustum[5][2] = this.clip[11] + this.clip[10];
        this.m_Frustum[5][3] = this.clip[15] + this.clip[14];
        this.normalizePlane(this.m_Frustum, 5);
    }

    //todo: hotspot
    public boolean cubeInFrustum(double x1, double y1, double z1, double x2, double y2, double z2) {
        for (int i = 0; i < 6; ++i) {
            if (!(
                    this.m_Frustum[i][0] * x1 +
                            this.m_Frustum[i][1] * y1 +
                            this.m_Frustum[i][2] * z1 +
                            this.m_Frustum[i][3] > 0.0F)

                    && !(this.m_Frustum[i][0] * x2 +
                    this.m_Frustum[i][1] * y1 +
                    this.m_Frustum[i][2] * z1 +
                    this.m_Frustum[i][3] > 0.0F)

                    && !(this.m_Frustum[i][0] * x1 +
                    this.m_Frustum[i][1] * y2 +
                    this.m_Frustum[i][2] * z1 +
                    this.m_Frustum[i][3] > 0.0F)

                    && !(this.m_Frustum[i][0] * x2 +
                    this.m_Frustum[i][1] * y2 +
                    this.m_Frustum[i][2] * z1 +
                    this.m_Frustum[i][3] > 0.0F)

                    && !(this.m_Frustum[i][0] * x1 +
                    this.m_Frustum[i][1] * y1 +
                    this.m_Frustum[i][2] * z2 +
                    this.m_Frustum[i][3] > 0.0F)

                    && !(this.m_Frustum[i][0] * x2 +
                    this.m_Frustum[i][1] * y1 +
                    this.m_Frustum[i][2] * z2 +
                    this.m_Frustum[i][3] > 0.0F)

                    && !(this.m_Frustum[i][0] * x1 +
                    this.m_Frustum[i][1] * y2 +
                    this.m_Frustum[i][2] * z2 +
                    this.m_Frustum[i][3] > 0.0F)

                    && !(this.m_Frustum[i][0] * x2 +
                    this.m_Frustum[i][1] * y2 +
                    this.m_Frustum[i][2] * z2 +
                    this.m_Frustum[i][3] > 0.0F)) {

                return false;
            }
        }
        return true;
    }

    // 判断一个点是否在视锥体内
    public boolean pointInFrustum(float x, float y, float z) {
        for (int i = 0; i < 6; i++) {
            float A = frustum[i * 4];
            float B = frustum[i * 4 + 1];
            float C = frustum[i * 4 + 2];
            float D = frustum[i * 4 + 3];

            if (A * x + B * y + C * z + D < 0) {
                return false;
            }
        }
        return true;
    }


    public boolean pointVisible(float x, float y, float z) {
        for (int i = 0; i < 6; i++) {
            float A = this.frustum[i * 4];
            float B = this.frustum[i * 4 + 1];
            float C = this.frustum[i * 4 + 2];
            float D = this.frustum[i * 4 + 3];

            if (A * x + B * y + C * z + D < 0) {
                return false;
            }
        }
        return true;
    }


    public boolean boxVisible(double x1, double y1, double z1, double x2, double y2, double z2) {
        for (int i = 0; i < 6; ++i) {
            if (!(this.frustum[i] * x1 + this.frustum[i * 4 + 1] * y1 + this.frustum[i * 4 + 2] * z1 + this.frustum[i * 4 + 3] > 0.0F) && !(this.frustum[i * 4] * x2 + this.frustum[i * 4 + 1] * y1 + this.frustum[i * 4 + 2] * z1 + this.frustum[i * 4 + 3] > 0.0F) && !(this.frustum[i * 4] * x1 + this.frustum[i * 4 + 1] * y2 + this.frustum[i * 4 + 2] * z1 + this.frustum[i * 4 + 3] > 0.0F) && !(this.frustum[i * 4] * x2 + this.frustum[i * 4 + 1] * y2 + this.frustum[i * 4 + 2] * z1 + this.frustum[i * 4 + 3] > 0.0F) && !(this.frustum[i * 4] * x1 + this.frustum[i * 4 + 1] * y1 + this.frustum[i * 4 + 2] * z2 + this.frustum[i * 4 + 3] > 0.0F) && !(this.frustum[i * 4] * x2 + this.frustum[i * 4 + 1] * y1 + this.frustum[i * 4 + 2] * z2 + this.frustum[i * 4 + 3] > 0.0F) && !(this.frustum[i * 4] * x1 + this.frustum[i * 4 + 1] * y2 + this.frustum[i * 4 + 2] * z2 + this.frustum[i * 4 + 3] > 0.0F) && !(this.frustum[i * 4] * x2 + this.frustum[i * 4 + 1] * y2 + this.frustum[i * 4 + 2] * z2 + this.frustum[i * 4 + 3] > 0.0F)) {

                return false;
            }
        }
        return true;
    }


    public boolean boxVisible(float x0, float y0, float z0, float x1, float y1, float z1) {
        if (true) {
            return cubeInFrustum(x0, y0, z0, x1, y1, z1);
        }

        if (pointVisible(x0, y0, z0)) {
            return true;
        }
        if (pointVisible(x1, y0, z0)) {
            return true;
        }
        if (pointVisible(x0, y1, z0)) {
            return true;
        }
        if (pointVisible(x1, y1, z0)) {
            return true;
        }
        if (pointVisible(x0, y0, z1)) {
            return true;
        }
        if (pointVisible(x1, y0, z1)) {
            return true;
        }
        if (pointVisible(x0, y1, z1)) {
            return true;
        }
        return pointVisible(x1, y1, z1);
    }

    public boolean boxVisible(Vector3d min, Vector3d max) {
        return boxVisible(min, max, 0, 0, 0);
    }

    public boolean boxVisible(Vector3d min, Vector3d max, Vector3d view) {
        return boxVisible(min, max, view.x, view.y, view.z);
    }

    public boolean boxVisible(Vector3d min, Vector3d max, double vx, double vy, double vz) {
        return boxVisible(
                (float) (min.x - vx),
                (float) (min.y - vy),
                (float) (min.z - vz),
                (float) (max.x - vz),
                (float) (max.y - vz),
                (float) (max.z - vz)
        );
    }

    public boolean boxVisible(AABB box) {
        return boxVisible(box, 0, 0, 0);
    }

    public boolean boxVisible(AABB box, Vector3d view) {
        return boxVisible(box, view.x, view.y, view.z);
    }

    public boolean boxVisible(AABB box, double vx, double vy, double vz) {
        return boxVisible(
                (float) (box.x0 - vx),
                (float) (box.y0 - vy),
                (float) (box.z0 - vz),
                (float) (box.x1 - vx),
                (float) (box.y1 - vy),
                (float) (box.z1 - vz)
        );
    }
}
