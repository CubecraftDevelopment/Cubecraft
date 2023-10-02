package ink.flybird.quantum3d_legacy;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;

public class ContextManager {
    public static void createLegacyGLContext(){
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_COMPAT_PROFILE);
        GL.createCapabilities();
    }

    public static void setGLContextVersion(int major, int minor) {
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, major);
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, minor);
    }
}
