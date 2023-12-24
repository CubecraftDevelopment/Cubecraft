package me.gb2022.quantum3d.lwjgl.deprecated;

import org.lwjgl.opengl.GL30;

public class FrameBuffer {
    int glID;

    public void alloc() {
        this.glID = GL30.glGenFramebuffers();
    }
}
