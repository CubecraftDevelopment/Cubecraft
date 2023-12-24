package me.gb2022.quantum3d.lwjgl;

import ink.flybird.fcommon.memory.BufferAllocator;
import me.gb2022.quantum3d.lwjgl.context.LWJGLBufferAllocator;
import me.gb2022.quantum3d.lwjgl.deprecated.GLUtil;
import me.gb2022.quantum3d.render.vertex.DrawMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryUtil;

import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;

public class TestBuilder {
    public static int ALLOCATED = 1;
    public static int RELEASED = 2;
    private final int size;
    private final DrawMode drawMode;
    private final DoubleBuffer vertexArray;
    private final FloatBuffer texCoordArray;
    private final FloatBuffer colorArray;
    private final FloatBuffer normalArray;
    private final DoubleBuffer rawArray;
    BufferAllocator allocator = new LWJGLBufferAllocator();
    private int count;
    private float u = 0.0f, v = 0.0f;
    private float r = 1.0f, g = 1.0f, b = 1.0f, a = 1.0f;
    private float n = 1.0f, f = 1.0f, l = 1.0f;
    private int status;

    public TestBuilder(int size, DrawMode drawMode) {

        this.size = size;
        this.drawMode = drawMode;
        this.status = ALLOCATED;
        this.vertexArray = allocator.allocDoubleBuffer(this.size * 3);
        this.texCoordArray = allocator.allocFloatBuffer(this.size * 3);
        this.colorArray = allocator.allocFloatBuffer(this.size * 4);
        this.normalArray = allocator.allocFloatBuffer(this.size * 3);
        this.rawArray = allocator.allocDoubleBuffer(this.size * 13);
    }

    public void tex(float u, float v) {
        this.u = u;
        this.v = v;
    }

    public void color(float r, float g, float b, float a) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
    }

    public void normal(float n, float f, float l) {
        this.n = n;
        this.f = f;
        this.l = l;
    }

    public DrawMode getDrawMode() {
        return drawMode;
    }

    public int getSize() {
        return size;
    }

    public int getCount() {
        return count;
    }

    public void free() {
        if (this.status != ALLOCATED) {
            return;
        }
        this.status = RELEASED;
        allocator.free(this.vertexArray);
        allocator.free(this.texCoordArray);
        allocator.free(this.colorArray);
        allocator.free(this.normalArray);
        allocator.free(this.rawArray);
    }

    public void vertex(double x, double y, double z) {
        this.vertexArray.put(x).put(y).put(z);
        this.texCoordArray.put(this.u).put(this.v);
        this.colorArray.put(this.r).put(this.g).put(this.b).put(this.a);
        this.normalArray.put(this.n).put(this.f).put(this.l);
        this.rawArray.put(this.u).put(this.v).put(this.r).put(this.g).put(this.b).put(this.a).put(this.n).put(this.f).put(this.l).put(x).put(y).put(z);
        this.count += 1;
    }

    public DoubleBuffer getVertexArray() {
        return this.vertexArray.flip();
    }

    public FloatBuffer getTexCoordArray() {
        return texCoordArray.flip();
    }

    public FloatBuffer getColorArray() {
        return colorArray.flip();
    }

    public FloatBuffer getNormalArray() {
        return normalArray.flip();
    }

    public DoubleBuffer getRawArray() {
        return rawArray.flip();
    }

    public void uploadPointer() {//TODO:OPTIONAL FORMAT
        GLUtil.enableClientState();
        GL11.glVertexPointer(3, GL11.GL_DOUBLE, 0, MemoryUtil.memAddress(this.getVertexArray()));
        GL11.glTexCoordPointer(2, GL11.GL_FLOAT, 0, MemoryUtil.memAddress(this.getTexCoordArray()));
        GL11.glColorPointer(4, GL11.GL_FLOAT, 0, MemoryUtil.memAddress(this.getColorArray()));
        GL11.glNormalPointer(GL11.GL_FLOAT, 0, MemoryUtil.memAddress(this.getNormalArray()));
        GL11.glDrawArrays(GL11.GL_QUADS, 0, this.getCount());
        GLUtil.disableClientState();
    }
}
