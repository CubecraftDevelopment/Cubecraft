package me.gb2022.quantum3d.render.vertex;

import ink.flybird.fcommon.memory.BufferAllocator;

import java.nio.ByteBuffer;

@SuppressWarnings("ClassCanBeRecord")
public final class DataFormat {
    public static final DataFormat EMPTY = null;

    public static final DataFormat FLOAT_2 = new DataFormat(3, DataType.FLOAT);
    public static final DataFormat FLOAT_3 = new DataFormat(3, DataType.FLOAT);
    public static final DataFormat FLOAT_4 = new DataFormat(4, DataType.FLOAT);

    private final int size;
    private final DataType type;

    public DataFormat(int size, DataType type) {
        this.size = size;
        this.type = type;
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

    public int getBufferCapacity(int vertexCount) {
        return vertexCount * this.type.getBytes() * this.getSize();
    }

    @Deprecated
    public ByteBuffer createBuffer(BufferAllocator allocator, int vertexCount) {
        return allocator.allocateBuffer(this.getBufferCapacity(vertexCount));
    }

    @Deprecated
    public void putToBuffer(ByteBuffer buffer, double... data) {
        for (int i = 0; i < this.getSize(); i++) {
            double d = data[i];
            switch (this.type) {
                case BYTE, UNSIGNED_BYTE -> buffer.put((byte) d);
                case SHORT, UNSIGNED_SHORT -> buffer.putShort((short) d);
                case INTEGER, UNSIGNED_INT -> buffer.putInt((int) d);
                case LONG, UNSIGNED_LONG -> buffer.putLong((long) d);
                case FLOAT -> buffer.putFloat((float) d);
                case DOUBLE -> buffer.putDouble(d);
            }
        }
    }
}
