package net.cubecraft.client.render.chunk.status;

import me.gb2022.commons.math.AABB;
import net.cubecraft.client.render.chunk.TerrainRenderer;

import java.util.Arrays;

public final class RenderChunkStatus {
    private final AABB aabb;
    private final int x;
    private final int y;
    private final int z;
    private final TerrainRenderer handle;
    private final boolean[] faceVisibility = new boolean[6];
    private int lastTestCX = Integer.MIN_VALUE;
    private int lastTestCZ = Integer.MAX_VALUE;
    private boolean frustumVisible;
    private ChunkUpdateStatus status;

    private long lastFrustumVisibilityUpdatedFrame;
    private long lastFaceVisibilityUpdateFrame;

    public RenderChunkStatus(TerrainRenderer handle, int x, int y, int z) {
        this.handle = handle;
        this.x = x;
        this.y = y;
        this.z = z;

        Arrays.fill(this.faceVisibility, false);
        this.frustumVisible = false;
        this.status = ChunkUpdateStatus.UPDATE_REQUIRED;
        this.aabb = new AABB(x * 16, y * 16, z * 16, x * 16 + 16, y * 16 + 16, z * 16 + 16);
    }

    public void updateFrustumVisibility(long frame, ChunkStatusHandler tester) {
        if (this.lastFrustumVisibilityUpdatedFrame >= frame) {
            return;
        }

        this.lastFrustumVisibilityUpdatedFrame = frame;

        this.frustumVisible = tester.testFrustum(this.x, this.y, this.z);
    }

    public void updateFaceVisibility(long frame, ChunkStatusHandler tester) {
        if (this.lastFaceVisibilityUpdateFrame >= frame) {
            return;
        }
        this.lastFaceVisibilityUpdateFrame = frame;

        tester.testFace(this.x, this.y, this.z, this.faceVisibility);
    }

    public boolean getFaceVisibility(int face) {
        return this.faceVisibility[face];
    }

    public boolean isFrustumVisible() {
        return frustumVisible;
    }

    public void setFrustumVisible(boolean frustumVisible) {
        this.frustumVisible = frustumVisible;
    }

    public void setFaceVisibility(int face, boolean faceVisibility) {
        this.faceVisibility[face] = faceVisibility;
    }

    public TerrainRenderer getHandle() {
        return handle;
    }

    public ChunkUpdateStatus getStatus() {
        return status;
    }

    public void setStatus(ChunkUpdateStatus status) {
        this.status = status;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public AABB getBounding() {
        return this.aabb;
    }

    public boolean checkFaceVisibilityDirty(int cx, int cz) {
        if (cx == this.lastTestCX && cz == this.lastTestCZ) {
            return false;
        }

        this.lastTestCX = cx;
        this.lastTestCZ = cz;

        return true;
    }
}
