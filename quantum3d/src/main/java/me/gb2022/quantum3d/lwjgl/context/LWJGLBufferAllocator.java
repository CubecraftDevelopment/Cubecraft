package me.gb2022.quantum3d.lwjgl.context;

import ink.flybird.fcommon.memory.BufferAllocator;
import org.lwjgl.system.MemoryUtil;

import java.nio.Buffer;
import java.nio.ByteBuffer;

public class LWJGLBufferAllocator extends BufferAllocator {
    public LWJGLBufferAllocator(int maxAllocateInstance, int maxAllocateCapacity) {
        super(maxAllocateInstance, maxAllocateCapacity);
    }

    public LWJGLBufferAllocator() {
        super();
    }

    @Override
    public ByteBuffer allocateBuffer(int size) {
        return MemoryUtil.memAlloc(size);
    }

    @Override
    public void freeBuffer(Buffer buffer) {
        MemoryUtil.memFree(buffer);
    }
}
