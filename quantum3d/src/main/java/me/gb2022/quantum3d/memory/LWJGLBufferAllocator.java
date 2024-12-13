package me.gb2022.quantum3d.memory;

import me.gb2022.commons.memory.BufferAllocator;
import org.lwjgl.system.MemoryUtil;

import java.nio.Buffer;
import java.nio.ByteBuffer;

public final class LWJGLBufferAllocator extends BufferAllocator {

    public LWJGLBufferAllocator(int maxAllocateInstance, int maxAllocateCapacity) {
        super(maxAllocateInstance, maxAllocateCapacity);
    }

    public LWJGLBufferAllocator() {
        super();
    }


    @Override
    public ByteBuffer allocateBuffer(int size) {
        return LWJGLSecureMemoryManager.allocate(size);
    }

    @Override
    public void freeBuffer(Buffer buffer) {
        LWJGLSecureMemoryManager.free(buffer);
    }

    @Override
    public long hashcode(Buffer buffer) {
        return MemoryUtil.memAddress(buffer);
    }
}
