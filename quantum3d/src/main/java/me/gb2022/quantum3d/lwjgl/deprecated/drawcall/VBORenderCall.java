package me.gb2022.quantum3d.lwjgl.deprecated.drawcall;

import me.gb2022.quantum3d.lwjgl.vertex.LocalVertexBuilder;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;

public class VBORenderCall implements IRenderCall {
    private int vbo;
    private int drawMode;
    private int count;
    private boolean allocated = false;
    private boolean uploaded = false;
    private int list;

    @Override
    public void call() {
        if (!this.allocated) {
            return;
        }
        if (!this.uploaded) {
            return;
        }
        if (!GL30.glIsBuffer(this.vbo)) {
            return;
        }
        if (this.count > 0) {
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, this.vbo);
            GL11.glCallList(this.list);
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        }
    }

    @Override
    public void upload(LocalVertexBuilder builder) {

    }

    @Override
    public void allocate() {
        if (!this.allocated) {
            this.list = GL11.glGenLists(1);
            this.vbo = GL15.glGenBuffers();
            this.allocated = true;
        }
    }

    @Override
    public void free() {
        GL11.glDeleteLists(this.list, 1);
        this.allocated = false;
        GL15.glDeleteBuffers(this.vbo);
    }

    @Override
    public boolean isAllocated() {
        return this.allocated;
    }

    @Override
    public int getHandle() {
        return this.vbo;
    }
}
