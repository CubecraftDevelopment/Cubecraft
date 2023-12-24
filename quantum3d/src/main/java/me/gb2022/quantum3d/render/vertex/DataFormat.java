package me.gb2022.quantum3d.render.vertex;

import ink.flybird.fcommon.memory.BufferAllocator;

import java.nio.ByteBuffer;

@SuppressWarnings("ClassCanBeRecord")
public final class DataFormat {
    private final int size;
    private final DataType type;

    public DataFormat(int size, DataType type) {
        this.size = size;
        this.type = type;
    }

    public static ByteBuffer createBuffer(BufferAllocator allocator, int vertexCount, DataFormat... fmtList) {
        return allocator.allocateBuffer(getDataLength(vertexCount, fmtList));
    }

    public static int getDataLength(int vertexCount, DataFormat... fmtList) {
        int len = 0;
        for (DataFormat fmt : fmtList) {
            if (fmt == null) {
                continue;
            }
            len += fmt.getBufferCapacity(vertexCount);
        }
        return len;
    }

    public DataType getType() {
        return this.type;
    }

    public int getSize() {
        return this.size;
    }

    public void check(double... data) {
        if (data.length == this.size) {
            return;
        }
        throw new RuntimeException("none matched data size %s, expected %s!".formatted(data.length, this.size));
    }

    public int getBufferCapacity(int vertexCount) {
        return vertexCount * this.type.getBytes();
    }

    public ByteBuffer createBuffer(BufferAllocator allocator, int vertexCount) {
        return allocator.allocateBuffer(this.getBufferCapacity(vertexCount));
    }

    public void putToBuffer(ByteBuffer buffer, double... data) {
        switch (this.type) {
            case BYTE, UNSIGNED_BYTE -> {
                for (double d : data) {
                    buffer.put((byte) d);
                }
            }
            case SHORT, UNSIGNED_SHORT -> {
                for (double d : data) {
                    buffer.putShort((short) d);
                }
            }
            case INTEGER, UNSIGNED_INT -> {
                for (double d : data) {
                    buffer.putInt((int) d);
                }
            }
            case LONG, UNSIGNED_LONG -> {
                for (double d : data) {
                    buffer.putLong((long) d);
                }
            }
            case FLOAT -> {
                for (double d : data) {
                    buffer.putFloat((float) d);
                }
            }
            case DOUBLE -> {
                for (double d : data) {
                    buffer.putDouble(d);
                }
            }
        }
    }
}
