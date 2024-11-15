package me.gb2022.quantum3d.lwjgl;

import me.gb2022.commons.memory.BufferAllocator;
import me.gb2022.quantum3d.memory.LWJGLBufferAllocator;
import me.gb2022.quantum3d.render.ShapeRenderer;
import me.gb2022.quantum3d.render.vertex.*;
import me.gb2022.quantum3d.util.GLUtil;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL31;

import java.nio.IntBuffer;

public class FrameBuffer {
    public static final BufferAllocator BUFFER_ALLOCATOR = new LWJGLBufferAllocator();
    public static final VertexBuilderAllocator BUILDER_ALLOCATOR = new VertexBuilderAllocator(BUFFER_ALLOCATOR);

    private int texture;
    private int depthTexture;
    private int buffer;
    private int width;
    private int height;

    private int renderWidth;
    private int renderHeight;

    public static void bindNone() {
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
    }

    public void allocate() {
        this.create(1024, 768);
    }


    public void create(int width, int height) {
        this.width = width;
        this.height = height;
        this.resizeRenderToScaledScreen(1.0f);

        this.buffer = GL30.glGenFramebuffers();
        this.texture = GL11.glGenTextures();
        this.depthTexture = GL11.glGenTextures();

        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, this.buffer);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.depthTexture);
        this.setTextureParams();
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_DEPTH_COMPONENT, this.renderWidth, this.renderHeight, 0, GL11.GL_DEPTH_COMPONENT, GL11.GL_FLOAT, (IntBuffer) null);
        GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, GL11.GL_TEXTURE_2D, this.depthTexture, 0);

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.texture);
        this.setTextureParams();
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, this.renderWidth, this.renderHeight, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, (IntBuffer) null);
        GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL11.GL_TEXTURE_2D, this.texture, 0);

        this.checkStatus();
        this.clear();
        GLUtil.checkError("frame_buffer:create_buffer");
    }

    public void free() {
        GL30.glDeleteFramebuffers(this.buffer);
        GL11.glDeleteTextures(this.texture);
        GL11.glDeleteTextures(this.depthTexture);
    }

    private void setTextureParams() {
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL31.GL_CLAMP_TO_EDGE);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL31.GL_CLAMP_TO_EDGE);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, 34892/*what the fuck*/, 0);
    }

    public void clear() {
        GL11.glClearColor(0, 0, 0, 0);
        GL11.glClearDepth(200);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
    }

    public void bindWrite() {
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, this.buffer);
        this.clear();
        GL11.glViewport(0, 0, this.width, this.height);
    }

    public void unbindWrite() {
        bindNone();
    }

    public void bindRead() {
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.texture);
    }

    public void bindDepthRead() {
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.depthTexture);
    }

    public void unbindRead() {
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(true);
    }

    public void checkStatus() {
        int i = GL30.glCheckFramebufferStatus(36160);
        if (i != 36053) {
            if (i == 36054) {
                throw new RuntimeException("GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT");
            } else if (i == 36055) {
                throw new RuntimeException("GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT");
            } else if (i == 36059) {
                throw new RuntimeException("GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER");
            } else if (i == 36060) {
                throw new RuntimeException("GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER");
            } else if (i == 36061) {
                throw new RuntimeException("GL_FRAMEBUFFER_UNSUPPORTED");
            } else if (i == 1285) {
                throw new RuntimeException("GL_OUT_OF_MEMORY");
            } else {
                throw new RuntimeException("glCheckFramebufferStatus returned unknown status:" + i);
            }
        }
    }


    public void resize(int width, int height) {
        if (this.width == width && this.height == height) {
            return;
        }
        if (this.buffer >= 0) {
            this.free();
        }
        this.create(width, height);
    }

    public void resizeRender(int width, int height) {
        this.renderWidth = width;
        this.renderHeight = height;
    }

    public void resizeRenderToScaledScreen(float scale) {
        this.resizeRender((int) (this.width * scale), (int) (this.height * scale));
    }

    public void upload(Runnable runnable) {
        GL11.glViewport(0, 0, this.renderWidth, this.renderHeight);
        this.bindWrite();
        runnable.run();
        this.unbindWrite();
        GL11.glViewport(0, 0, this.width, this.height);
    }

    public void blit() {
        GLUtil.enableBlend();
        VertexBuilder builder = BUILDER_ALLOCATOR.create(VertexFormat.V3F_C4F_T2F, DrawMode.QUADS, 8);
        builder.allocate();
        GLUtil.enableBlend();
        builder.setColor(1, 1, 1, 1);
        GL11.glViewport(0, 0, this.renderWidth, this.renderHeight);
        this.bindRead();
        ShapeRenderer.drawRectUV(builder, 0, 100, 0, 0, 100, 0, 1, 1, 0);
        VertexBuilderUploader.uploadPointer(builder);
        builder.free();
        this.unbindRead();
    }
}
