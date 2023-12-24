package me.gb2022.quantum3d.render.vertex;

import java.nio.Buffer;
import java.nio.ByteBuffer;

public enum DataType {
    BYTE(byte.class, ByteBuffer.class, 1),
    UNSIGNED_BYTE(byte.class, ByteBuffer.class, 1),
    SHORT(short.class, ByteBuffer.class, 2),
    UNSIGNED_SHORT(short.class, ByteBuffer.class, 2),
    INTEGER(int.class, ByteBuffer.class, 4),
    UNSIGNED_INT(int.class, ByteBuffer.class, 4),
    LONG(long.class, ByteBuffer.class, 8),
    UNSIGNED_LONG(long.class, ByteBuffer.class, 8),
    FLOAT(float.class, ByteBuffer.class, 4),
    DOUBLE(double.class, ByteBuffer.class, 8);

    private final Class<?> clazz;
    private final Class<? extends Buffer> arrayType;
    private final int bytes;

    DataType(Class<?> clazz, Class<? extends Buffer> arrayType, int bytes) {
        this.clazz = clazz;
        this.arrayType = arrayType;
        this.bytes = bytes;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public int getBytes() {
        return bytes;
    }

    public Class<? extends Buffer> getArrayType() {
        return arrayType;
    }
}
