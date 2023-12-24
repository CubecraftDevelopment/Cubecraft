package me.gb2022.quantum3d.lwjgl;

import ink.flybird.fcommon.memory.BufferAllocator;

import java.nio.*;

public record WrappedBuffer(Buffer buffer) {

    public void put(double... data) {
        if (this.buffer instanceof ByteBuffer) {
            for (double d : data) {
                ((ByteBuffer) this.buffer).put((byte) d);
            }
            return;
        }
        if (this.buffer instanceof ShortBuffer) {
            for (double d : data) {
                ((ShortBuffer) this.buffer).put((short) d);
            }
            return;
        }
        if (this.buffer instanceof IntBuffer) {
            for (double d : data) {
                ((IntBuffer) this.buffer).put((int) d);
            }
            return;
        }
        if (this.buffer instanceof LongBuffer) {
            for (double d : data) {
                ((LongBuffer) this.buffer).put((long) d);
            }
            return;
        }
        if (this.buffer instanceof FloatBuffer) {
            for (double d : data) {
                ((FloatBuffer) this.buffer).put((float) d);
            }
            return;
        }
        if (this.buffer instanceof DoubleBuffer) {
            ((DoubleBuffer) this.buffer).put(data);
            return;
        }
        throw new IllegalStateException("wtf is this buffer?" + this.buffer.getClass());
    }


    public void free(BufferAllocator allocator) {
        if (this.buffer instanceof ByteBuffer) {
            allocator.free((ByteBuffer) this.buffer);
            return;
        }
        if (this.buffer instanceof ShortBuffer) {
            allocator.free((ShortBuffer) this.buffer);
            return;
        }
        if (this.buffer instanceof IntBuffer) {
            allocator.free((IntBuffer) this.buffer);
            return;
        }
        if (this.buffer instanceof LongBuffer) {
            allocator.free((LongBuffer) this.buffer);
            return;
        }
        if (this.buffer instanceof FloatBuffer) {
            allocator.free((FloatBuffer) this.buffer);
            return;
        }
        if (this.buffer instanceof DoubleBuffer) {
            allocator.free((DoubleBuffer) this.buffer);
        }
    }
}
