package me.gb2022.quantum3d.lwjgl.texture;

import me.gb2022.quantum3d.ITextureImage;
import me.gb2022.quantum3d.lwjgl.deprecated.GLUtil;
import me.gb2022.quantum3d.render.texture.SimpleTexture2D;
import org.lwjgl.opengl.GL11;

import java.nio.ByteBuffer;

public abstract class OGLTexture2D extends OGLTexture implements SimpleTexture2D {
    protected int width;
    protected int height;

    @Override
    public int getBindingType() {
        return GL11.GL_TEXTURE_2D;
    }

    @Override
    public int getWidth() {
        return this.width;
    }

    @Override
    public int getHeight() {
        return this.height;
    }

    @Override
    public void upload(ITextureImage image) {
        this.width = image.getWidth();
        this.height = image.getHeight();
        this.name = image.getName();
        this.bind();
        ByteBuffer buffer = ByteBuffer.wrap(image.getPixels());
        GL11.glTexImage2D(this.getBindingType(), 0, GL11.GL_RGBA, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
        GLUtil.checkError("load");
    }
}
