package me.gb2022.quantum3d.lwjgl.context;

import ink.flybird.fcommon.memory.BufferAllocator;
import me.gb2022.quantum3d.lwjgl.TypeAdapter;
import me.gb2022.quantum3d.lwjgl.batching.OGLListRenderCall;
import me.gb2022.quantum3d.lwjgl.batching.OGLListRenderCallAllocator;
import me.gb2022.quantum3d.lwjgl.context.LWJGLBufferAllocator;
import me.gb2022.quantum3d.lwjgl.deprecated.BufferAllocation;
import me.gb2022.quantum3d.lwjgl.deprecated.GLUtil;
import me.gb2022.quantum3d.lwjgl.texture.OGLSimpleTexture2D;
import me.gb2022.quantum3d.lwjgl.texture.OGLTilemapTexture2D;
import me.gb2022.quantum3d.lwjgl.vertex.BufferedVertexBuilderAllocator;
import me.gb2022.quantum3d.render.RenderContext;
import me.gb2022.quantum3d.render.command.RenderCall;
import me.gb2022.quantum3d.render.command.RenderCallAllocator;
import me.gb2022.quantum3d.render.texture.SimpleTexture2D;
import me.gb2022.quantum3d.render.texture.TilemapTexture2D;
import me.gb2022.quantum3d.render.vertex.VertexBuilder;
import me.gb2022.quantum3d.render.vertex.VertexBuilderAllocator;
import org.joml.Matrix4d;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryUtil;

import java.nio.DoubleBuffer;

import static me.gb2022.quantum3d.lwjgl.deprecated.GLUtil.loadIdentity;


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
        GL.setCapabilities(null);
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
        loadIdentity();
        DoubleBuffer matrix = BufferAllocation.allocDoubleBuffer(16);
        GL11.glMultMatrixd(mat.get(matrix));
        BufferAllocation.free(matrix);
        GL11.glMatrixMode(5888);
        loadIdentity();
    }

    public void mov(double x, double y, double z, double xr, double yr, double zr) {
        GL11.glRotated(xr, 1, 0, 0);
        GL11.glRotated(yr, 0, 1, 0);
        GL11.glRotated(zr, 0, 0, 1);
        GL11.glTranslated(x, y, z);
    }


    @Override
    public VertexBuilderAllocator newVertexBuilderAllocator() {
        return new BufferedVertexBuilderAllocator(new LWJGLBufferAllocator());
    }

    @Override
    public VertexBuilderAllocator newVertexBuilderAllocator(BufferAllocator allocator) {
        return new BufferedVertexBuilderAllocator(allocator);
    }

    @Override
    public void setViewport(int x, int y, int width, int height) {
        GL11.glViewport(x, y, width, height);
    }

    @Override
    public void uploadVertexBuilder(VertexBuilder builder) {
        GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
        GL11.glVertexPointer(
                builder.getVertexFormat().getSize(),
                TypeAdapter.dataTypeToGLID(builder.getVertexFormat().getType()),
                0,
                MemoryUtil.memAddress(builder.getVertexData())
        );
        if (builder.getTextureFormat() != null) {
            GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
            GL11.glTexCoordPointer(
                    builder.getTextureFormat().getSize(),
                    TypeAdapter.dataTypeToGLID(builder.getTextureFormat().getType()),
                    0,
                    MemoryUtil.memAddress(builder.getTextureData())
            );
        }
        if (builder.getColorFormat() != null) {
            GL11.glEnableClientState(GL11.GL_COLOR_ARRAY);
            GL11.glColorPointer(
                    builder.getColorFormat().getSize(),
                    TypeAdapter.dataTypeToGLID(builder.getColorFormat().getType()),
                    0,
                    MemoryUtil.memAddress(builder.getColorData())
            );
        }
        if (builder.getNormalFormat() != null) {
            GL11.glEnableClientState(GL11.GL_NORMAL_ARRAY);
            GL11.glNormalPointer(
                    TypeAdapter.dataTypeToGLID(builder.getNormalFormat().getType()),
                    0,
                    MemoryUtil.memAddress(builder.getNormalData())
            );
        }
        this.checkError("upload_builder:data_upload");

        GL11.glDrawArrays(TypeAdapter.DrawModeToGLID(builder.getDrawMode()), 0, builder.getVertexCount());
        this.checkError("upload_builder:draw_array");

        GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);
        if (builder.getTextureFormat() != null) {
            GL11.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
        }
        if (builder.getColorFormat() != null) {
            GL11.glDisableClientState(GL11.GL_COLOR_ARRAY);
        }
        if (builder.getNormalFormat() != null) {
            GL11.glDisableClientState(GL11.GL_NORMAL_ARRAY);
        }
        this.checkError("upload_builder:close_state");
    }

    @Override
    public RenderCallAllocator newRenderCallAllocator() {
        return new OGLListRenderCallAllocator();
    }

    @Override
    public RenderCall newRenderCall() {
        return new OGLListRenderCall();
    }

    @Override
    public void checkError() {

    }
}
