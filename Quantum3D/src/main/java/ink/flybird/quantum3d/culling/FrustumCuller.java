package ink.flybird.quantum3d.culling;

import ink.flybird.quantum3d.BufferAllocation;
import ink.flybird.quantum3d.Camera;
import ink.flybird.fcommon.math.AABB;
import org.joml.FrustumIntersection;
import org.lwjgl.opengl.GL11;

import java.nio.FloatBuffer;


public class FrustumCuller extends ICuller {
    public final float[][] m_Frustum = new float[6][4];
    final float[] proj = new float[16];
    final float[] modl = new float[16];
    final float[] clip = new float[16];
    private final FloatBuffer _proj = BufferAllocation.allocFloatBuffer(16);
    private final FloatBuffer _modl = BufferAllocation.allocFloatBuffer(16);
    private final FloatBuffer _clip = BufferAllocation.allocFloatBuffer(16);
    private final FrustumIntersection frustumIntersection = new FrustumIntersection();

    public FrustumCuller(Camera camera) {
        super(camera);
    }

    @Override
    public void update() {
        this.frustumIntersection.set(camera.getCurrentMatrix());
    }

    private void normalizePlane(float[][] frustum, int side) {
        float magnitude = (float) Math.sqrt(frustum[side][0] * frustum[side][0] + frustum[side][1] * frustum[side][1] + frustum[side][2] * frustum[side][2]);
        frustum[side][0] /= magnitude;
        frustum[side][1] /= magnitude;
        frustum[side][2] /= magnitude;
        frustum[side][3] /= magnitude;
    }

    public void calculateFrustum() {
        this._proj.clear();
        this._modl.clear();
        this._clip.clear();
        GL11.glGetFloatv(GL11.GL_PROJECTION_MATRIX, _proj);
        GL11.glGetFloatv(GL11.GL_MODELVIEW_MATRIX, _modl);
        this._proj.flip().limit(16);
        this._proj.get(this.proj);
        this._modl.flip().limit(16);
        this._modl.get(this.modl);
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

    @Override
    public boolean aabbVisible(AABB aabb) {
        return this.cubeInFrustum((float) aabb.x0, (float) aabb.y0, (float) aabb.z0, (float) aabb.x1, (float) aabb.y1, aabb.z1);
    }

}
