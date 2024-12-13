package me.gb2022.quantum3d.memory;

import me.gb2022.commons.memory.BlockedBufferAllocator;
import me.gb2022.commons.memory.BufferAllocator;
import org.lwjgl.system.MemoryUtil;

import java.nio.Buffer;
import java.nio.ByteBuffer;

public final class LWJGLBlockedBufferAllocator extends BlockedBufferAllocator {
    public LWJGLBlockedBufferAllocator(ByteBuffer handle, int blockSize, int blockCount) {
        super(handle, blockSize, blockCount);
    }

    public LWJGLBlockedBufferAllocator(BufferAllocator handle, int blockSize, int blockCount) {
        super(handle, blockSize, blockCount);
    }

    @Override
    public long address(Buffer buffer) {
        return MemoryUtil.memAddress(buffer);
    }

    @Override
    public ByteBuffer wrap(long l, int i) {
        return MemoryManager.wrap(l, i);
    }
}
