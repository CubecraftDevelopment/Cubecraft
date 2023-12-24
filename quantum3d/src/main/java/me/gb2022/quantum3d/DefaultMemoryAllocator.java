package me.gb2022.quantum3d;

import ink.flybird.fcommon.memory.BufferAllocator;

import java.nio.Buffer;
import java.nio.ByteBuffer;

public class DefaultMemoryAllocator extends BufferAllocator {
    @Override
    public ByteBuffer allocateBuffer(int i) {
        return ByteBuffer.allocateDirect(i);
    }

    @Override
    public void freeBuffer(Buffer buffer) {
        //((sun.nio.ch.DirectBuffer) buffer).cleaner().clean();
    }
}
