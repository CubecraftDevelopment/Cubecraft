package me.gb2022.quantum3d.lwjgl.context;

import org.lwjgl.glfw.GLFW;

public final class CoreOGLRenderContext extends OGLRenderContext {
    public CoreOGLRenderContext(int majorVersion, int minorVersion) {
        super(majorVersion, minorVersion);
    }

    @Override
    public int getGLFWProfileID() {
        return GLFW.GLFW_OPENGL_CORE_PROFILE;
    }
}
