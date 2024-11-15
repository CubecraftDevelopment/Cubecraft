package me.gb2022.quantum3d.lwjgl.context;

import me.gb2022.quantum3d.lwjgl.batching.GLRenderList;
import me.gb2022.quantum3d.render.texture.OGLSimpleTexture2D;
import me.gb2022.quantum3d.render.texture.OGLTilemapTexture2D;
import me.gb2022.quantum3d.render.RenderContext;
import me.gb2022.quantum3d.render.command.RenderCall;
import me.gb2022.quantum3d.render.texture.SimpleTexture2D;
import me.gb2022.quantum3d.render.texture.TilemapTexture2D;
import me.gb2022.quantum3d.render.vertex.VertexBuilder;
import me.gb2022.quantum3d.util.BufferAllocation;
import me.gb2022.quantum3d.util.GLUtil;
import org.joml.Matrix4d;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;

import java.nio.DoubleBuffer;

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
    public void setDepthTest() {
        GLUtil.enableDepthTest();
    }

    @Override
    public SimpleTexture2D texture2d() {
        return new OGLSimpleTexture2D();
    }

    @Override
    public TilemapTexture2D tilemapTexture2d(int maxImageWidth, int maxImageHeight, int minSampleX, int minSampleY) {
        return new OGLTilemapTexture2D(maxImageWidth, maxImageHeight, minSampleX, minSampleY);
    }

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
    public void setBufferClearColor(double r, double g, double b) {
        GL11.glClearColor((float) r, (float) g, (float) b, 1.0f);
    }

    @Override
    public void clearMatrix() {
        GL11.glLoadIdentity();
    }

    @Override
    public void setMatrix(Matrix4d mat) {
        GLUtil.assertRenderThread();
        GL11.glMatrixMode(5889);
        GLUtil.loadIdentity();
        DoubleBuffer matrix = BufferAllocation.allocDoubleBuffer(16);
        GL11.glMultMatrixd(mat.get(matrix));
        BufferAllocation.free(matrix);
        GL11.glMatrixMode(5888);
        GLUtil.loadIdentity();
    }

    @Override
    public void setViewport(int x, int y, int width, int height) {
        GL11.glViewport(x, y, width, height);
    }

    @Override
    public void uploadVertexBuilder(VertexBuilder builder) {

    }

    @Override
    public RenderCall newRenderCall() {
        return new GLRenderList();
    }

    @Override
    public void checkError() {

    }
}
