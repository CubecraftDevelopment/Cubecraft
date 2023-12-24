package me.gb2022.quantum3d.lwjgl.batching;

import me.gb2022.quantum3d.lwjgl.deprecated.GLUtil;
import me.gb2022.quantum3d.lwjgl.deprecated.drawcall.DrawCallException;
import me.gb2022.quantum3d.render.command.RenderCall;
import org.lwjgl.opengl.GL11;

public class OGLListRenderCall implements RenderCall {
    private int list;
    private boolean allocated = false;

    @Override
    public void call() {
        if (!this.allocated) {
            throw new DrawCallException("not initialized!");
        }
        if (!GL11.glIsList(this.list)) {
            throw new DrawCallException("not a list!");
        }
        GL11.glCallList(this.list);
        GLUtil.checkError("call");
    }

    @Override
    public void upload(Runnable command) {
        if (!this.allocated) {
            throw new DrawCallException("not initialized!");
        }
        if (!GL11.glIsList(this.list)) {
            throw new DrawCallException("not a list!");
        }
        GL11.glNewList(this.list, GL11.GL_COMPILE_AND_EXECUTE);
        GLUtil.checkError("begin_record");
        command.run();
        GL11.glEndList();
        GLUtil.checkError("end_record");
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
        GLUtil.checkError("destroy");
        GL11.glDeleteLists(this.list, 1);
        this.allocated = false;
    }
}
