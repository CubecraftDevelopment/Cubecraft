package me.gb2022.quantum3d.lwjgl.context;

import me.gb2022.quantum3d.render.RenderContext;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;

public abstract class OGLRenderContext extends RenderContext {
    private final int majorVersion;
    private final int minorVersion;

    protected OGLRenderContext(int majorVersion, int minorVersion) {
        this.majorVersion = majorVersion;
        this.minorVersion = minorVersion;
    }

    @Override
    public void create() {
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, this.majorVersion);
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, this.minorVersion);
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, this.getGLFWProfileID());
        GL.createCapabilities();
    }

    @Override
    public void destroy() {
    }

    public abstract int getGLFWProfileID();

    @Override
    public void clearBuffer() {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glClearDepth(1.0f);
        GL11.glDisable(GL11.GL_CULL_FACE);
    }

    @Override
    public void checkError(String status) {
        int errorStatus = GL11.glGetError();
        if (errorStatus == 0) {
            return;
        }

        String errorCode = switch (errorStatus) {
            case GL11.GL_STACK_OVERFLOW -> "stack_over_flow";
            case GL11.GL_STACK_UNDERFLOW -> "stack_down_flow";
            case GL11.GL_INVALID_OPERATION -> "invalid_operation";
            case GL11.GL_INVALID_ENUM -> "invalid_enum";
            case GL11.GL_INVALID_VALUE -> "invalid_value";
            default -> "unverified_error";
        };
        throw new RuntimeException("%s[%d]:%s".formatted(status, errorStatus, errorCode));
    }

    @Override
    public void checkError() {

    }
}
