package ink.flybird.quantum3d.memory;

import ink.flybird.fcommon.memory.BufferAllocator;
import org.lwjgl.system.MemoryUtil;

import java.nio.Buffer;
import java.nio.ByteBuffer;

public class DirectBufferAllocator extends BufferAllocator {
    @Override
    public ByteBuffer allocateBuffer(int size) {
        return MemoryUtil.memAlloc(size);
    }

    @Override
    public void freeBuffer(Buffer buffer) {
        MemoryUtil.memFree(buffer);
    }
}
