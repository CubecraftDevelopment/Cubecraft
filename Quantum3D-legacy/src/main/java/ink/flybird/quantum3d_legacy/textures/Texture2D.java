package ink.flybird.quantum3d_legacy.textures;

import ink.flybird.quantum3d_legacy.BufferAllocation;
import ink.flybird.quantum3d_legacy.GLUtil;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL32;
import org.lwjgl.opengl.GL33;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;

public class Texture2D extends Texture {
    public Texture2D(boolean ms, boolean mip) {
        super(ms, mip);
    }

    @Override
    public Texture2D load(ITextureImage image) {
        if (image == null) {
            return this;
        }
        BufferedImage img = image.getAsImage();
        this.width = img.getWidth();
        this.height = img.getHeight();
        this.bind();
        ByteBuffer buffer = ImageUtil.getByteFromBufferedImage_RGBA(img);

        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
        GL33.glGenerateMipmap(GL11.GL_TEXTURE_2D);

        BufferAllocation.free(buffer);
        GLUtil.checkError("load");
        return this;
    }

    public Texture2D load(BufferedImage img) {
        this.width = img.getWidth();
        this.height = img.getHeight();
        this.bind();
        ByteBuffer buffer = ImageUtil.getByteFromBufferedImage_RGBA(img);
        GL11.glTexImage2D(this.getBindingType(), 0, GL11.GL_RGBA, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
        BufferAllocation.free(buffer);
        GLUtil.checkError("load");
        return this;
    }

    @Override
    public int getBindingType() {
        if (this.multiSample) {
            return GL32.GL_TEXTURE_2D_MULTISAMPLE;
        } else {
            return GL11.GL_TEXTURE_2D;
        }
    }
}
