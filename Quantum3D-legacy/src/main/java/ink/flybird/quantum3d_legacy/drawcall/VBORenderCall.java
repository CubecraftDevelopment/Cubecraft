package ink.flybird.quantum3d_legacy.drawcall;

import ink.flybird.quantum3d_legacy.GLUtil;
import ink.flybird.quantum3d_legacy.draw.VertexBuilder;
import ink.flybird.quantum3d_legacy.draw.VertexUploader;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;

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
        if (this.count <= 0) {
            return;
        }
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, this.vbo);

        GL11.glTexCoordPointer(2, GL11.GL_FLOAT, 12 * 4, 0);
        GL11.glColorPointer(4, GL11.GL_FLOAT, 12 * 4, 8);
        GL11.glNormalPointer(GL11.GL_FLOAT, 12 * 4, 24);
        GL11.glVertexPointer(3, GL11.GL_FLOAT, 12 * 4, 36);

        GLUtil.enableClientState();
        GLUtil.drawArrays(this.drawMode, 0, this.count);
        GLUtil.disableClientState();

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        VertexUploader.UPLOAD_COUNTER.addAndGet(this.count);
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
