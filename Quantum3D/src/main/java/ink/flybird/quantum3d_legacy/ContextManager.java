package ink.flybird.quantum3d_legacy;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;

public class ContextManager {
    //jni glfw
    public static void initGLFW() {
        GLFW.glfwInit();
    }

    public static void destroyGLFW() {
        GLFW.glfwTerminate();
    }

    public static void createLegacyGLContext(){
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_COMPAT_PROFILE);
        GL.createCapabilities();
    }

    public static void createModernGLContext(){
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE);
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_FORWARD_COMPAT, GLFW.GLFW_TRUE);
    }

    public static void setGLContextVersion(int major, int minor) {
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, major);
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, minor);
    }
}
