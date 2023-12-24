package me.gb2022.quantum3d.render.vertex;

import ink.flybird.fcommon.context.LifetimeCounter;

import java.nio.Buffer;
import java.util.concurrent.atomic.AtomicInteger;


public abstract class VertexBuilder {
    protected final AtomicInteger vertexCount = new AtomicInteger();
    private final DataFormat vertexFormat;
    private final DataFormat textureFormat;
    private final DataFormat colorFormat;
    private final DataFormat normalFormat;
    private final LifetimeCounter lifetimeCounter = new LifetimeCounter();
    private final DrawMode drawMode;
    private final int capacity;

    /**
     * Constructs a VertexBuilder with specified parameters.
     *
     * @param capacity      The maximum capacity of the builder.
     * @param drawMode      The draw mode for rendering.
     * @param vertexFormat  The format for vertex data.
     * @param textureFormat The format for texture data.
     * @param colorFormat   The format for color data.
     * @param normalFormat  The format for normal data.
     */
    public VertexBuilder(
            int capacity,
            DrawMode drawMode,
            DataFormat vertexFormat,
            DataFormat textureFormat,
            DataFormat colorFormat,
            DataFormat normalFormat
    ) {
        this.vertexFormat = vertexFormat;
        this.textureFormat = textureFormat;
        this.colorFormat = colorFormat;
        this.normalFormat = normalFormat;
        this.drawMode = drawMode;
        this.capacity = capacity;
    }

    /**
     * Checks whether the given data matches the specified element's size.
     *
     * @param element The vertex format element to check against.
     * @param data    The data to be checked.
     * @throws IllegalArgumentException if the data size does not match the element size.
     */
    public static void checkRange(DataFormat element, double... data) {
        if (element == null) {
            throw new IllegalArgumentException("No data channel provided!");
        }
        if (data.length != element.getSize()) {
            throw new IllegalArgumentException(
                    "Non-matched vertex size: %d required, %d provided"
                            .formatted(element.getSize(), data.length)
            );
        }
    }

    /**
     * Allocates resources for the vertex builder.
     */
    public final void allocate() {
        this.lifetimeCounter.allocate();
        this.alloc();
    }

    /**
     * Frees allocated resources.
     */
    public final void free() {
        this.lifetimeCounter.release();
        this.dealloc();
    }

    /**
     * Adds a vertex with the specified data.
     *
     * @param data The vertex data to be added.
     */
    public final void addVertex(double... data) {
        if (this.vertexCount.get() >= this.capacity) {
            throw new RuntimeException("Builder overflowed: reached capacity " + this.capacity);
        }
        checkRange(this.vertexFormat, data);
        this.vertex(data);
        this.vertexCount.incrementAndGet();
    }

    /**
     * Sets texture coordinates with the specified data.
     *
     * @param data The texture coordinate data to be set.
     */
    public final void setTextureCoord(double... data) {
        checkRange(this.textureFormat, data);
        this.textureCoord(data);
    }

    /**
     * Sets color with the specified data.
     *
     * @param data The color data to be set.
     */
    public final void setColor(double... data) {
        checkRange(this.colorFormat, data);
        this.color(data);
    }

    /**
     * Sets normal with the specified data.
     *
     * @param data The normal data to be set.
     */
    public final void setNormal(double... data) {
        checkRange(this.normalFormat, data);
        this.normal(data);
    }

    /**
     * Adds vertex data to the builder. Subclasses should implement this method.
     *
     * @param data The vertex data to be added.
     */
    public abstract void vertex(double... data);

    /**
     * Adds texture coordinate data to the builder. Subclasses should implement this method.
     *
     * @param data The texture coordinate data to be added.
     */
    public abstract void textureCoord(double... data);

    /**
     * Adds color data to the builder. Subclasses should implement this method.
     *
     * @param data The color data to be added.
     */
    public abstract void color(double... data);

    /**
     * Adds normal data to the builder. Subclasses should implement this method.
     *
     * @param data The normal data to be added.
     */
    public abstract void normal(double... data);

    /**
     * Allocates resources for the vertex builder. Subclasses should implement this method.
     */
    public abstract void alloc();

    /**
     * Deallocates resources for the vertex builder. Subclasses should implement this method.
     */
    public abstract void dealloc();

    /**
     * Returns the buffer containing the vertex data.
     *
     * @return The vertex data buffer.
     */
    public abstract Buffer getVertexData();

    /**
     * Returns the buffer containing the texture data.
     *
     * @return The texture data buffer.
     */
    public abstract Buffer getTextureData();

    /**
     * Returns the buffer containing the color data.
     *
     * @return The color data buffer.
     */
    public abstract Buffer getColorData();

    /**
     * Returns the buffer containing the normal data.
     *
     * @return The normal data buffer.
     */
    public abstract Buffer getNormalData();

    /**
     * Returns the buffer containing the raw data.
     *
     * @return The raw data buffer.
     */
    public abstract Buffer getRawData();

    /**
     * Get the color format of the vertex builder.
     *
     * @return The color format.
     */
    public DataFormat getColorFormat() {
        return this.colorFormat;
    }

    /**
     * Get the normal format of the vertex builder.
     *
     * @return The normal format.
     */
    public DataFormat getNormalFormat() {
        return this.normalFormat;
    }

    /**
     * Get the texture format of the vertex builder.
     *
     * @return The texture format.
     */
    public DataFormat getTextureFormat() {
        return this.textureFormat;
    }

    /**
     * Get the vertex format of the vertex builder.
     *
     * @return The vertex format.
     */
    public DataFormat getVertexFormat() {
        return this.vertexFormat;
    }

    /**
     * Get the current vertex count in the builder.
     *
     * @return The current vertex count.
     */
    public int getVertexCount() {
        return this.vertexCount.get();
    }

    /**
     * Get the draw mode of the vertex builder.
     *
     * @return The draw mode.
     */
    public DrawMode getDrawMode() {
        return this.drawMode;
    }

    /**
     * Get the capacity of the vertex builder.
     *
     * @return The capacity.
     */
    public int getCapacity() {
        return this.capacity;
    }
}
