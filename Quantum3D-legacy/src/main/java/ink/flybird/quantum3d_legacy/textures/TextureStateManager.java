package ink.flybird.quantum3d_legacy.textures;

import org.lwjgl.opengl.*;

public class TextureStateManager {
    public static void setTextureClamp(Texture t,boolean clamp){
        t.bind();
        if (clamp) {
            GL21.glTexParameteri(3553, GL11.GL_TEXTURE_WRAP_S, GL13.GL_CLAMP_TO_BORDER);
            GL11.glTexParameteri(3553, GL11.GL_TEXTURE_WRAP_T, GL13.GL_CLAMP_TO_BORDER);
        }
        else {
            GL11.glTexParameteri(3553, 10242, 10497);
            GL11.glTexParameteri(3553, 10243, 10497);
        }
        t.unbind();
    }

    public static void setTextureMipMap(Texture t,boolean mipMap){
        t.bind();
        if(mipMap){
            GL11.glTexParameteri(t.getBindingType(),GL32.GL_TEXTURE_MAX_LOD,4);
            if(t instanceof Texture2DTileMap){
                GL11.glTexParameteri(t.getBindingType(), GL11.GL_TEXTURE_MAG_FILTER,GL11.GL_NEAREST);
                GL11.glTexParameteri(t.getBindingType(), GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST_MIPMAP_LINEAR);
            }else{
                GL11.glTexParameteri(t.getBindingType(), GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
                GL11.glTexParameteri(t.getBindingType(), GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR);
            }
            GL30.glGenerateMipmap(t.getBindingType());
        }else{
            GL11.glTexParameteri(t.getBindingType(),GL32.GL_TEXTURE_MAX_LOD,1);
            GL11.glTexParameteri(t.getBindingType(), GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
            GL11.glTexParameteri(t.getBindingType(), GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
            GL30.glGenerateMipmap(t.getBindingType());
        }
        t.unbind();
    }

    public static void setTextureBlur(Texture t,boolean blur,int level){
        t.bind();
        GL11.glTexParameteri(t.getBindingType(), GL32.GL_TEXTURE_MIN_LOD, blur?level:0);
        setTextureMipMap(t,blur);
        t.unbind();
    }
}
