package ink.flybird.quantum3d.drawcall;

import ink.flybird.quantum3d.GLUtil;
import ink.flybird.quantum3d.draw.VertexBuilder;
import ink.flybird.quantum3d.draw.VertexUploader;
import org.lwjgl.opengl.GL11;

public class ListRenderCall implements IRenderCall {
    private int list;
    private boolean allocated = false;
    private int count;

    public ListRenderCall(int i) {
        this.list = i;
        this.allocated = true;
    }

    public ListRenderCall() {
    }

    @Override
    public void call() {
        if (!this.allocated) {
            throw new DrawCallException("not initialized!");
        }
        if (!GL11.glIsList(this.list)) {
            throw new DrawCallException("not a list!");
        }
        GL11.glCallList(this.list);
        VertexUploader.UPLOAD_COUNTER.addAndGet(this.count);
    }

    @Override
    public void upload(VertexBuilder builder) {
        if (!this.allocated) {
            throw new DrawCallException("not initialized!");
        }
        if (!GL11.glIsList(this.list)) {
            throw new DrawCallException("not a list!");
        }
        GL11.glNewList(this.list, GL11.GL_COMPILE);
        builder.uploadPointer();
        GL11.glEndList();
        this.count = builder.getCount();
    }

    @Override
    public void allocate() {
        if (this.allocated) {
            throw new DrawCallException("initialized");
        }
        this.list = GL11.glGenLists(1);
        GLUtil.checkError("list_allocate");
        this.allocated = true;
    }

    @Override
    public void free() {
        if (!this.allocated) {
            throw new DrawCallException("not initialized");
        }
        if (!GL11.glIsList(this.list)) {
            throw new DrawCallException("not a list!");
        }
        GLUtil.checkError("destroy");
        GL11.glDeleteLists(this.list, 1);
        this.allocated=false;
    }

    @Override
    public boolean isAllocated() {
        return this.allocated;
    }

    @Override
    public int getHandle() {
        return this.list;
    }
}
