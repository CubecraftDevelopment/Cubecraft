package me.gb2022.quantum3d.render.vertex;

import me.gb2022.commons.memory.BufferAllocator;

import java.util.concurrent.atomic.AtomicInteger;

public final class VertexBuilderAllocator {
    private final AtomicInteger counter = new AtomicInteger();
    private final BufferAllocator allocator;

    public VertexBuilderAllocator(BufferAllocator allocator) {
        this.allocator = allocator;
    }

    public void free(VertexBuilder builder) {
        if (builder.getAllocator() != this) {
            throw new IllegalArgumentException("not from here");
        }
        builder.free();
    }

    public VertexBuilder create(VertexFormat format, DrawMode mode, int capacity) {
        this.counter.incrementAndGet();
        return new VertexBuilder(format, capacity, mode, this);
    }

    public VertexBuilder allocate(VertexFormat format, DrawMode mode, int capacity) {
        this.counter.incrementAndGet();
        var b = new VertexBuilder(format, capacity, mode, this);
        b.allocate();
        return b;
    }

    public BufferAllocator getAllocator() {
        return allocator;
    }

    public AtomicInteger getCounter() {
        return counter;
    }

    public void clearInstance(VertexBuilder vertexBuilder) {
        this.counter.decrementAndGet();
    }
}
