package me.gb2022.quantum3d.memory;

import org.lwjgl.system.MemoryUtil;

import java.nio.*;
import java.util.concurrent.atomic.AtomicLong;

public interface LWJGLSecureMemoryManager {
    AtomicLong INSTANCES = new AtomicLong(0);
    AtomicLong USAGE = new AtomicLong(0);

    static ByteBuffer allocate(int size) {
        INSTANCES.incrementAndGet();
        USAGE.addAndGet(size);
        return MemoryUtil.memAlloc(size);
    }

    static void free(Buffer buffer) {
        MemoryUtil.memFree(buffer);
        INSTANCES.decrementAndGet();

        if (buffer instanceof ByteBuffer) {
            USAGE.addAndGet(-1 * buffer.capacity());
            return;
        }
        if (buffer instanceof ShortBuffer) {
            USAGE.addAndGet(-2L * buffer.capacity());
            return;
        }
        if (buffer instanceof IntBuffer) {
            USAGE.addAndGet(-4L * buffer.capacity());
            return;
        }
        if (buffer instanceof LongBuffer) {
            USAGE.addAndGet(-8L * buffer.capacity());
            return;
        }
        if (buffer instanceof FloatBuffer) {
            USAGE.addAndGet(-4L * buffer.capacity());
            return;
        }
        if (buffer instanceof DoubleBuffer) {
            USAGE.addAndGet(-8L * buffer.capacity());
        }
    }
}
