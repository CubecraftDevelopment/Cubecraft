package me.gb2022.quantum3d.render.texture;

import me.gb2022.quantum3d.texture.ITextureImage;
import me.gb2022.quantum3d.util.GLUtil;
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
}
