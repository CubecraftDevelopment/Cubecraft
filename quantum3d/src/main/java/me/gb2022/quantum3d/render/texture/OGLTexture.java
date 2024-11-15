package me.gb2022.quantum3d.render.texture;

import me.gb2022.commons.LifetimeCounter;
import me.gb2022.quantum3d.util.GLUtil;
import org.lwjgl.opengl.GL11;

public abstract class OGLTexture implements Texture {
    private final LifetimeCounter counter = new LifetimeCounter();
    protected String name;
    private int textureHandle;

    public abstract int getBindingType();

    @Override
    public void bind() {
        GLUtil.checkError("texture_bind:pre");
        GL11.glEnable(this.getBindingType());
        GL11.glBindTexture(this.getBindingType(), this.textureHandle);
        GLUtil.checkError("texture_bind:post");
    }

    @Override
    public void unbind() {
        GLUtil.checkError("texture_unbind:pre");
        GL11.glBindTexture(this.getBindingType(), 0);
        GL11.glDisable(this.getBindingType());
        GLUtil.checkError("texture_unbind:post");
    }

    @Override
    public void allocate() {
        GLUtil.checkError("texture_allocate:pre");
        this.counter.allocate();
        this.textureHandle = GL11.glGenTextures();
        this.bind();
        GL11.glTexParameteri(this.getBindingType(), GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(this.getBindingType(), GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
        GLUtil.checkError("texture_allocate:post");
    }

    @Override
    public void delete() {
        GLUtil.checkError("texture_delete:pre");
        this.counter.release();
        GL11.glDeleteTextures(this.textureHandle);
        GLUtil.checkError("texture_delete:post");
    }

    @Override
    public String getName() {
        return name;
    }
}
