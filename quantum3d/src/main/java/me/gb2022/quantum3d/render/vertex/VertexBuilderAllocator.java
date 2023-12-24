package me.gb2022.quantum3d.render.vertex;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * The abstract class VertexBuilderAllocator provides a mechanism for allocating and freeing VertexBuilder instances.
 * Subclasses of this class should implement the create method to instantiate specific VertexBuilder implementations.
 */
public abstract class VertexBuilderAllocator {

    /**
     * An AtomicInteger to keep track of the number of allocated VertexBuilders.
     */
    public final AtomicInteger counter = new AtomicInteger();

    /**
     * Allocates a new VertexBuilder instance with specified parameters.
     *
     * @param mode          The draw mode for rendering.
     * @param capacity      The maximum capacity of the builder.
     * @param vertexFormat  The format for vertex data.
     * @param textureFormat The format for texture data.
     * @param colorFormat   The format for color data.
     * @param normalFormat  The format for normal data.
     * @return The allocated VertexBuilder instance.
     */
    public final VertexBuilder allocate(
            DrawMode mode,
            int capacity,
            DataFormat vertexFormat,
            DataFormat textureFormat,
            DataFormat colorFormat,
            DataFormat normalFormat
    ) {
        this.counter.incrementAndGet();
        VertexBuilder builder = this.create(
                mode,
                capacity,
                vertexFormat,
                textureFormat,
                colorFormat,
                normalFormat
        );
        builder.allocate();
        return builder;
    }

    /**
     * Frees the allocated resources of a VertexBuilder instance.
     *
     * @param builder The VertexBuilder instance to free.
     */
    public final void free(VertexBuilder builder) {
        this.counter.decrementAndGet();
        builder.free();
    }

    /**
     * Creates a new VertexBuilder instance with specified parameters. Subclasses should implement this method.
     *
     * @param mode          The draw mode for rendering.
     * @param capacity      The maximum capacity of the builder.
     * @param vertexFormat  The format for vertex data.
     * @param textureFormat The format for texture data.
     * @param colorFormat   The format for color data.
     * @param normalFormat  The format for normal data.
     * @return The created VertexBuilder instance.
     */
    public abstract VertexBuilder create(
            DrawMode mode,
            int capacity,
            DataFormat vertexFormat,
            DataFormat textureFormat,
            DataFormat colorFormat,
            DataFormat normalFormat
    );
}
