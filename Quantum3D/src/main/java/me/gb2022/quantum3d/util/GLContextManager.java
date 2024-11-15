package me.gb2022.quantum3d.util;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;

public interface GLContextManager {
    //jni glfw
    static void initGLFW() {
        GLFW.glfwInit();
    }

    static void destroyGLFW() {
        GLFW.glfwTerminate();
    }

    static void createLegacyGLContext() {
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_COMPAT_PROFILE);
        GL.createCapabilities();
    }

    static void createModernGLContext() {
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE);
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_FORWARD_COMPAT, GLFW.GLFW_TRUE);
    }

    static void setGLContextVersion(int major, int minor) {
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, major);
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, minor);
    }
}
