package me.gb2022.quantum3d.render.vertex;

import me.gb2022.commons.context.LifetimeCounter;
import me.gb2022.commons.memory.BufferAllocator;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;


public class VertexBuilder {
    protected final AtomicInteger vertexCount = new AtomicInteger();
    private final VertexFormat format;
    private final LifetimeCounter lifetimeCounter = new LifetimeCounter();
    private final DrawMode drawMode;
    private final int capacity;

    private final BufferAllocator allocator;
    private ByteBuffer vertexBuffer;
    private ByteBuffer colorBuffer;
    private ByteBuffer textureBuffer;
    private ByteBuffer normalBuffer;
    private ByteBuffer rawBuffer;

    private double[] colorCache;
    private double[] textureCache;
    private double[] normalCache;


    public VertexBuilder(VertexFormat format, int capacity, DrawMode drawMode, BufferAllocator allocator) {
        this.format = format;
        this.drawMode = drawMode;
        this.capacity = capacity;
        this.allocator = allocator;
    }

    public void allocate() {
        int cap = this.getCapacity();

        this.lifetimeCounter.allocate();

        this.vertexBuffer = this.allocator.allocateBuffer(this.format.getVertexBufferSize(cap));
        this.colorBuffer = this.allocator.allocateBuffer(this.format.getColorBufferSize(cap));
        this.textureBuffer = this.allocator.allocateBuffer(this.format.getTextureBufferSize(cap));
        this.normalBuffer = this.allocator.allocateBuffer(this.format.getNormalBufferSize(cap));
        this.rawBuffer = this.allocator.allocateBuffer(this.format.getRawBufferSize(cap));

        if (this.format.hasColorData()) {
            this.colorCache = new double[this.format.getColorFormat().getSize()];
        }
        if (this.format.hasTextureData()) {
            this.textureCache = new double[this.format.getTextureFormat().getSize()];
        }
        if (this.format.hasNormalData()) {
            this.normalCache = new double[this.format.getNormalFormat().getSize()];
        }

        if (this.colorCache == null) {
            return;
        }
        Arrays.fill(this.colorCache, 1.0f);
    }

    public void free() {
        this.lifetimeCounter.release();
        this.allocator.free(this.vertexBuffer);
        this.allocator.free(this.colorBuffer);
        this.allocator.free(this.textureBuffer);
        this.allocator.free(this.normalBuffer);
        this.allocator.free(this.rawBuffer);
    }

    public final void addVertex(double... data) {
        if (this.vertexCount.get() >= this.capacity) {
            throw new RuntimeException("Builder overflowed: reached capacity " + this.capacity);
        }
        this.vertexCount.incrementAndGet();
        this.format.putVertexData(this.vertexBuffer, this.rawBuffer, data);
        this.format.putColorData(this.colorBuffer, this.rawBuffer, this.colorCache);
        this.format.putTextureData(this.textureBuffer, this.rawBuffer, this.textureCache);
        this.format.putNormalData(this.normalBuffer, this.rawBuffer, this.normalCache);
    }

    public final void setColor(double... data) {
        this.colorCache = data;
    }

    public final void setTextureCoordinate(double... data) {
        this.textureCache = data;
    }

    public final void setNormal(double... data) {
        this.normalCache = data;
    }

    public int getVertexCount() {
        return this.vertexCount.get();
    }

    public DrawMode getDrawMode() {
        return this.drawMode;
    }

    public int getCapacity() {
        return this.capacity;
    }

    public void reset() {
        this.vertexCount.set(0);
        Arrays.fill(this.colorCache, 1.0f);
        this.vertexBuffer.clear().position(0);
        this.textureBuffer.clear().position(0);
        this.colorBuffer.clear().position(0);
        this.normalBuffer.clear().position(0);
        this.rawBuffer.clear().position(0);
    }

    public VertexFormat getFormat() {
        return format;
    }


    //buffer reference
    public ByteBuffer getVertexBuffer() {
        return vertexBuffer;
    }

    public ByteBuffer getColorBuffer() {
        return colorBuffer;
    }

    public ByteBuffer getTextureBuffer() {
        return textureBuffer;
    }

    public ByteBuffer getNormalBuffer() {
        return normalBuffer;
    }

    public ByteBuffer getRawBuffer() {
        return rawBuffer;
    }

    //generate buffer
    public ByteBuffer generateVertexBuffer() {
        return getVertexBuffer().slice(0, this.format.getVertexBufferSize(this.getVertexCount()));
    }

    public ByteBuffer generateColorBuffer() {
        return getColorBuffer().slice(0, this.format.getColorBufferSize(this.getVertexCount()));
    }

    public ByteBuffer generateTextureBuffer() {
        return getTextureBuffer().slice(0, this.format.getTextureBufferSize(this.getVertexCount()));
    }

    public ByteBuffer generateNormalBuffer() {
        return getNormalBuffer().slice(0, this.format.getNormalBufferSize(this.getVertexCount()));
    }

    public ByteBuffer generateRawBuffer() {
        return getRawBuffer().slice(0, this.format.getRawBufferSize(this.getVertexCount()));
    }
}
