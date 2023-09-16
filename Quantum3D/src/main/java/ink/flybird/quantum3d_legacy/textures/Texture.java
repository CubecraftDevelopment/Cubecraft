package ink.flybird.quantum3d_legacy.textures;

import ink.flybird.quantum3d_legacy.GLUtil;
import ink.flybird.fcommon.logging.Logger;
import ink.flybird.fcommon.logging.SimpleLogger;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL45;

public abstract class Texture {
    protected final boolean multiSample;
    protected final boolean mipMap;
    protected final Logger logger = new SimpleLogger("Texture");
    protected int glId;
    protected int width;
    protected int height;
    private boolean generated;

    public Texture(boolean multiSample, boolean mipMap) {
        this.multiSample = multiSample;
        this.mipMap = mipMap;
        this.generateTexture();
    }

    //operation
    public void bind() {
        GL11.glEnable(this.getBindingType());
        GL11.glBindTexture(this.getBindingType(), this.glId);
        GLUtil.checkError("texture_binding");
    }

    public void unbind() {
        GLUtil.checkError("pre_texture_unbind");
        GL11.glDisable(this.getBindingType());
        GLUtil.checkError("post_texture_unbind");
    }

    public abstract int getBindingType();

    //load
    public void generateTexture() {
        if (this.generated) {
            return;
        }
        this.generated = true;

        int[] i = new int[1];
        GL45.glGenTextures(i);
        this.glId = i[0];
        this.bind();
        if (mipMap) {
            GL11.glTexParameteri(this.getBindingType(), GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR);
        } else {
            GL11.glTexParameteri(this.getBindingType(), GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        }
        GL11.glTexParameteri(this.getBindingType(), GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
        GLUtil.checkError("generate_texture");
    }

    public abstract Texture load(ITextureImage image);

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public void destroy() {
        GL11.glDeleteTextures(this.glId);
    }
}
