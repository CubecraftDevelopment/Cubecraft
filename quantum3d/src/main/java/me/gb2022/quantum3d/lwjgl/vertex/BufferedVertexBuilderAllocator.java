package me.gb2022.quantum3d.lwjgl.vertex;

import ink.flybird.fcommon.memory.BufferAllocator;
import me.gb2022.quantum3d.Quantum3d;
import me.gb2022.quantum3d.render.vertex.DataFormat;
import me.gb2022.quantum3d.render.vertex.DrawMode;
import me.gb2022.quantum3d.render.vertex.VertexBuilder;
import me.gb2022.quantum3d.render.vertex.VertexBuilderAllocator;

public final class BufferedVertexBuilderAllocator extends VertexBuilderAllocator {
    private final BufferAllocator allocator;

    public BufferedVertexBuilderAllocator(BufferAllocator allocator) {
        this.allocator = allocator;
    }

    public BufferedVertexBuilderAllocator() {
        this(Quantum3d.BUFFER_ALLOCATOR);
    }

    @Override
    public VertexBuilder create(DrawMode mode, int capacity, DataFormat vertexFormat, DataFormat textureFormat, DataFormat colorFormat, DataFormat normalFormat) {
        return new BufferVertexBuilder(
                capacity,
                mode,
                vertexFormat,
                textureFormat,
                colorFormat,
                normalFormat,
                this.allocator
        );
    }

    public BufferAllocator getAllocator() {
        return allocator;
    }
}
