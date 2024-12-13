package me.gb2022.quantum3d.lwjgl.context;

import me.gb2022.quantum3d.util.camera.Camera;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL20;

public final class CoreOGLRenderContext extends OGLRenderContext {
    public CoreOGLRenderContext(int majorVersion, int minorVersion) {
        super(majorVersion, minorVersion);
    }

    @Override
    public int getGLFWProfileID() {
        return GLFW.GLFW_OPENGL_CORE_PROFILE;
    }


    public void setMatrix(Camera camera) {

    }
}
