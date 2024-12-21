package me.gb2022.quantum3d.util.camera;

import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;

//todo:hotspot 2
public interface MatrixAppender {
    FloatBuffer SWAP_F = MemoryUtil.memAllocFloat(16);//at least 16

    static void setProjectionMatrix(Matrix4f mat) {
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();

        mat.get(SWAP_F);

        GL11.glMultMatrixf(SWAP_F);
    }

    static void setModelViewMatrix(Matrix4f mat) {
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glLoadIdentity();

        mat.get(SWAP_F);

        GL11.glMultMatrixf(SWAP_F);
    }


}