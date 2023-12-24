package me.gb2022.quantum3d.lwjgl.vertex;

import ink.flybird.fcommon.memory.BufferAllocator;
import me.gb2022.quantum3d.render.vertex.DataFormat;
import me.gb2022.quantum3d.render.vertex.DrawMode;

import java.nio.ByteBuffer;

public final class BufferVertexBuilder extends LocalVertexBuilder {
    private final BufferAllocator allocator;
    private final int rawDataSize;
    private ByteBuffer rawData;
    private ByteBuffer vertexData;
    private ByteBuffer textureCoordinateData;
    private ByteBuffer colorData;
    private ByteBuffer normalData;

    public BufferVertexBuilder(
            int capacity,
            DrawMode drawMode,
            DataFormat vertexFormat,
            DataFormat textureFormat,
            DataFormat colorFormat,
            DataFormat normalFormat,
            BufferAllocator allocator
    ) {
        super(
                capacity,
                drawMode,
                vertexFormat,
                textureFormat,
                colorFormat,
                normalFormat
        );
        this.allocator = allocator;
        this.rawDataSize = DataFormat.getDataLength(
                this.getCapacity(),
                this.getVertexFormat(),
                this.getTextureFormat(),
                this.getColorFormat(),
                this.getNormalFormat()
        );
    }

    private ByteBuffer createBuffer(DataFormat fmt) {
        return fmt.createBuffer(this.allocator, this.getCapacity());
    }

    private void putData(ByteBuffer channel, DataFormat fmt, double[] data) {
        if (fmt == null) {
            return;
        }
        fmt.check(data);
        fmt.putToBuffer(channel, data);
        fmt.putToBuffer(this.rawData, data);
    }

    @Override
    public void alloc() {
        this.vertexData = this.createBuffer(this.getVertexFormat());
        this.textureCoordinateData = this.createBuffer(this.getTextureFormat());
        this.colorData = this.createBuffer(this.getColorFormat());
        this.normalData = this.createBuffer(this.getNormalFormat());
        this.rawData = this.allocator.allocateBuffer(this.rawDataSize);
    }

    @Override
    public void dealloc() {
        this.allocator.freeBuffer(this.vertexData);
        this.allocator.freeBuffer(this.textureCoordinateData);
        this.allocator.freeBuffer(this.colorData);
        this.allocator.freeBuffer(this.normalData);
        this.allocator.free(this.rawData);
    }

    public void vertex(double... data) {
        this.vertexCount.incrementAndGet();
        this.putData(this.vertexData, this.getVertexFormat(), data);
        this.putData(this.textureCoordinateData, this.getTextureFormat(), this.textureCoordCache);
        this.putData(this.colorData, this.getColorFormat(), this.colorCache);
        this.putData(this.normalData, this.getNormalFormat(), this.normalCache);
    }


    @Override
    public ByteBuffer getVertexData() {
        return this.vertexData.slice(0, this.getDataLength(this.getVertexFormat()));
    }

    @Override
    public ByteBuffer getTextureData() {
        return this.textureCoordinateData.slice(0, this.getDataLength(this.getTextureFormat()));
    }

    @Override
    public ByteBuffer getColorData() {
        return this.colorData.slice(0, this.getDataLength(this.getColorFormat()));
    }

    @Override
    public ByteBuffer getNormalData() {
        return this.normalData.slice(0, this.getDataLength(this.getNormalFormat()));
    }

    @Override
    public ByteBuffer getRawData() {
        return this.rawData.slice();
    }

    private int getDataLength(DataFormat format) {
        return format.getSize() * this.getVertexCount();
    }
}
