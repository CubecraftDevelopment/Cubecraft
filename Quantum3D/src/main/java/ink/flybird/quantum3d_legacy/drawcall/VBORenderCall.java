package ink.flybird.quantum3d_legacy.drawcall;

import ink.flybird.quantum3d_legacy.GLUtil;
import ink.flybird.quantum3d_legacy.draw.VertexBuilder;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;

public class VBORenderCall implements IRenderCall {
    private int vbo;
    private int drawMode;
    private int count;
    private boolean allocated = false;
    private boolean uploaded = false;

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

            GL11.glTexCoordPointer(2, GL11.GL_DOUBLE, 12 * 8, 0);
            GL11.glColorPointer(4, GL11.GL_DOUBLE, 12 * 8, 16);
            GL11.glNormalPointer(GL11.GL_DOUBLE, 12 * 8, 48);
            GL11.glVertexPointer(3, GL11.GL_DOUBLE, 12 * 8, 72);

            GLUtil.enableClientState();
            GLUtil.drawArrays(this.drawMode, 0, this.count);
            GLUtil.disableClientState();
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        }
    }

    @Override
    public void upload(VertexBuilder builder) {
        if (!this.allocated) {
            throw new DrawCallException("not initialized");
        }
        this.drawMode = builder.getDrawMode().getGlMode();
        this.count = builder.getCount();
        builder.uploadBuffer(this.vbo);
        this.uploaded = true;

    }

    @Override
    public void allocate() {
        if (!this.allocated) {
            this.vbo = GL15.glGenBuffers();
            this.allocated = true;
        }
    }

    @Override
    public void free() {
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
