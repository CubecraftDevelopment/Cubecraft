package me.gb2022.quantum3d.render.vertex;

import me.gb2022.commons.LifetimeCounter;
import me.gb2022.commons.memory.BufferAllocator;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;


public final class VertexBuilder {
    private final VertexBuilderAllocator vertexBuilderAllocator;
    private final AtomicInteger vertexCount = new AtomicInteger();
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


    public VertexBuilder(VertexFormat format, int capacity, DrawMode drawMode, VertexBuilderAllocator allocator) {
        this.format = format;
        this.drawMode = drawMode;
        this.capacity = capacity;
        this.vertexBuilderAllocator = allocator;
        this.allocator = allocator.getAllocator();
    }

    public void allocate() {
        int cap = this.getCapacity();

        this.lifetimeCounter.allocate();

        this.vertexBuffer = this.allocator.allocByteBuffer(this.format.getVertexBufferSize(cap));
        this.rawBuffer = this.allocator.allocByteBuffer(this.format.getRawBufferSize(cap));

        if (this.format.hasColorData()) {
            this.colorBuffer = this.allocator.allocByteBuffer(this.format.getColorBufferSize(cap));
        }
        if (this.format.hasTextureData()) {
            this.textureBuffer = this.allocator.allocByteBuffer(this.format.getTextureBufferSize(cap));
        }
        if (this.format.hasNormalData()) {
            this.normalBuffer = this.allocator.allocByteBuffer(this.format.getNormalBufferSize(cap));
        }


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

    public void freeReferenced() {
        this.vertexBuilderAllocator.free(this);
    }

    public void free() {
        if (!this.lifetimeCounter.isAllocated()) {
           return;
        }

        if (this.vertexBuffer != null) {
            this.allocator.free(this.vertexBuffer);
        }
        if (this.rawBuffer != null) {
            this.allocator.free(this.rawBuffer);
        }

        if (this.format.hasColorData() && this.colorBuffer != null) {
            this.allocator.free(this.colorBuffer);
        }
        if (this.format.hasTextureData() && this.textureBuffer != null) {
            this.allocator.free(this.textureBuffer);
        }
        if (this.format.hasNormalData() && this.normalBuffer != null) {
            this.allocator.free(this.normalBuffer);
        }

        this.vertexBuilderAllocator.clearInstance(this);
        this.lifetimeCounter.release();
    }

    public void addVertex(double... data) {
        if (this.vertexCount.get() >= this.capacity) {
            throw new RuntimeException("Builder overflowed: reached capacity " + this.capacity);
        }
        this.vertexCount.incrementAndGet();
        this.format.putVertexData(this.vertexBuffer, this.rawBuffer, data);
        this.format.putColorData(this.colorBuffer, this.rawBuffer, this.colorCache);
        this.format.putTextureData(this.textureBuffer, this.rawBuffer, this.textureCache);
        this.format.putNormalData(this.normalBuffer, this.rawBuffer, this.normalCache);
    }

    public VertexBuilder setColor(double... data) {
        this.colorCache = data;
        return this;
    }

    public VertexBuilder setTextureCoordinate(double... data) {
        this.textureCache = data;
        return this;
    }

    public VertexBuilder setNormal(double... data) {
        this.normalCache = data;
        return this;
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
        if (this.colorCache != null) {
            Arrays.fill(this.colorCache, 1.0f);
        }
        this.vertexBuffer.clear().position(0);

        if (this.format.hasColorData()) {
            this.colorBuffer.clear().position(0);
        }
        if (this.format.hasTextureData()) {
            this.textureBuffer.clear().position(0);
        }
        if (this.format.hasNormalData()) {
            this.normalBuffer.clear().position(0);
        }
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

    public LifetimeCounter getLifetimeCounter() {
        return lifetimeCounter;
    }

    public VertexBuilderAllocator getAllocator() {
        return vertexBuilderAllocator;
    }

    @Override
    protected void finalize() {
        if (!this.lifetimeCounter.isAllocated()) {
            return;
        }

        System.out.println("Un-expected freed buffer: " + this.toString());

        this.vertexBuilderAllocator.free(this);
    }

    @Override
    public String toString() {
        return "VertexBuilder{" + "vertexCount=" + vertexCount + ", format=" + format + ", lifetimeCounter=" + lifetimeCounter + ", drawMode=" + drawMode + ", capacity=" + capacity + ", allocator=" + allocator + '}';
    }
}
