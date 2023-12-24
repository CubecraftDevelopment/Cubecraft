package me.gb2022.quantum3d.lwjgl.deprecated;

import org.lwjgl.system.MemoryUtil;

import java.nio.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * traceable buffer allocation.I HATE YOU NIO BUFFER!
 *
 * @author GrassBlock2022
 */
public interface BufferAllocation {
    AtomicInteger alloc = new AtomicInteger(0);
    AtomicInteger instances = new AtomicInteger(0);
    AtomicInteger leaked = new AtomicInteger(0);


    static ByteBuffer allocByteBuffer(int size) {
        checkSize();
        alloc.addAndGet(size);
        instances.addAndGet(1);
        return MemoryUtil.memAlloc(size);
    }

    static ShortBuffer allocShortBuffer(int size) {
        checkSize();
        alloc.addAndGet(size * 2);
        instances.addAndGet(1);
        return MemoryUtil.memAlloc(size * 2).asShortBuffer();
    }

    static IntBuffer allocIntBuffer(int size) {
        checkSize();
        alloc.addAndGet(size * 4);
        instances.addAndGet(1);
        return MemoryUtil.memAlloc(size * 4).asIntBuffer();
    }

    static FloatBuffer allocFloatBuffer(int size) {
        checkSize();
        alloc.addAndGet(size * 4);
        instances.addAndGet(1);
        return MemoryUtil.memAlloc(size * 4).asFloatBuffer();
    }

    static LongBuffer allocLongBuffer(int size) {
        checkSize();
        alloc.addAndGet(size * 8);
        instances.addAndGet(1);
        return MemoryUtil.memAlloc(size * 8).asLongBuffer();
    }

    static DoubleBuffer allocDoubleBuffer(int size) {
        checkSize();
        alloc.addAndGet(size * 8);
        instances.addAndGet(1);
        return MemoryUtil.memAlloc(size * 8).asDoubleBuffer();
    }

    static void free(ByteBuffer buffer) {
        alloc.addAndGet(-buffer.capacity());
        instances.addAndGet(-1);
        MemoryUtil.memFree(buffer);
    }

    static void free(ShortBuffer buffer) {
        alloc.addAndGet(-buffer.capacity() * 2);
        instances.addAndGet(-1);
        MemoryUtil.memFree(buffer);
    }

    static void free(IntBuffer buffer) {
        alloc.addAndGet(-buffer.capacity() * 4);
        instances.addAndGet(-1);
        MemoryUtil.memFree(buffer);
    }

    static void free(FloatBuffer buffer) {
        alloc.addAndGet(-buffer.capacity() * 4);
        instances.addAndGet(-1);
        MemoryUtil.memFree(buffer);
    }

    static void free(LongBuffer buffer) {
        alloc.addAndGet(-buffer.capacity() * 8);
        instances.addAndGet(-1);
        MemoryUtil.memFree(buffer);
    }

    static void free(DoubleBuffer buffer) {
        alloc.addAndGet(-buffer.capacity() * 8);
        instances.addAndGet(-1);
        MemoryUtil.memFree(buffer);
    }

    static long getAllocSize() {
        return alloc.intValue();
    }

    static int getAllocInstances() {
        return instances.intValue();
    }

    private static void checkSize() {
        if (instances.get() > 16384) {
            throw new Error("off heap overflowed(%d buffers)".formatted(instances.get()));
        }
        if (alloc.get() > 67108864 * 8) {
            throw new Error("off heap overflowed(%d bytes)".formatted(alloc.get()));
        }
    }
}
