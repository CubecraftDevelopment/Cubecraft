package me.gb2022.quantum3d.memory;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.*;
import java.util.Objects;
import java.util.function.LongPredicate;

import static org.lwjgl.system.Pointer.BITS32;
import static org.lwjgl.system.jni.JNINativeInterface.NewDirectByteBuffer;

/**
 * org.lwjgl.system.MemoryUtil
 */
public final class MemoryManager {
    public static final long MARK;
    public static final long POSITION;
    public static final long LIMIT;
    public static final long CAPACITY;
    public static final long ADDRESS;
    public static final int MAGIC_CAPACITY = 0x0D15EA5E;
    public static final int MAGIC_POSITION = 0x00FACADE;
    static final sun.misc.Unsafe UNSAFE;
    static final Class<? extends ByteBuffer> BUFFER_BYTE;
    static final Class<? extends ShortBuffer> BUFFER_SHORT;
    static final Class<? extends CharBuffer> BUFFER_CHAR;
    static final Class<? extends IntBuffer> BUFFER_INT;
    static final Class<? extends LongBuffer> BUFFER_LONG;
    static final Class<? extends FloatBuffer> BUFFER_FLOAT;
    static final Class<? extends DoubleBuffer> BUFFER_DOUBLE;

    static {
        ByteBuffer bb = ByteBuffer.allocateDirect(0).order(ByteOrder.nativeOrder());

        BUFFER_BYTE = bb.getClass();
        BUFFER_SHORT = bb.asShortBuffer().getClass();
        BUFFER_CHAR = bb.asCharBuffer().getClass();
        BUFFER_INT = bb.asIntBuffer().getClass();
        BUFFER_LONG = bb.asLongBuffer().getClass();
        BUFFER_FLOAT = bb.asFloatBuffer().getClass();
        BUFFER_DOUBLE = bb.asDoubleBuffer().getClass();

        UNSAFE = getUnsafeInstance();

        try {
            MARK = getMarkOffset();
            POSITION = getPositionOffset();
            LIMIT = getLimitOffset();
            CAPACITY = getCapacityOffset();

            ADDRESS = getAddressOffset();

        } catch (Throwable t) {
            throw new UnsupportedOperationException(t);
        }
    }

    public static long address(Buffer buffer) {
        return UNSAFE.getLong(buffer, ADDRESS);
    }

    public static ByteBuffer wrap(long address, int capacity) {
        ByteBuffer buffer;
        try {
            buffer = (ByteBuffer) UNSAFE.allocateInstance(BUFFER_BYTE);
        } catch (InstantiationException e) {
            throw new UnsupportedOperationException(e);
        }

        UNSAFE.putLong(buffer, ADDRESS, address);
        UNSAFE.putInt(buffer, MARK, -1);
        UNSAFE.putInt(buffer, LIMIT, capacity);
        UNSAFE.putInt(buffer, CAPACITY, capacity);

        return buffer.order(ByteOrder.nativeOrder());
    }


    public static sun.misc.Unsafe getUnsafeInstance() {
        java.lang.reflect.Field[] fields = sun.misc.Unsafe.class.getDeclaredFields();

    /*
    Different runtimes use different names for the Unsafe singleton,
    so we cannot use .getDeclaredField and we scan instead. For example:

    Oracle: theUnsafe
    PERC : m_unsafe_instance
    Android: THE_ONE
    */
        for (java.lang.reflect.Field field : fields) {
            if (!field.getType().equals(sun.misc.Unsafe.class)) {
                continue;
            }

            int modifiers = field.getModifiers();
            if (!(java.lang.reflect.Modifier.isStatic(modifiers) && java.lang.reflect.Modifier.isFinal(modifiers))) {
                continue;
            }

            try {
                field.setAccessible(true);
                return (sun.misc.Unsafe) field.get(null);
            } catch (Exception ignored) {
            }
            break;
        }

        throw new UnsupportedOperationException("LWJGL requires sun.misc.Unsafe to be available.");
    }

    public static long getFieldOffsetInt(Object container, int value) {
        return getFieldOffset(container.getClass(), int.class, offset -> UNSAFE.getInt(container, offset) == value);
    }

    public static long getMarkOffset() {
        ByteBuffer bb = Objects.requireNonNull(NewDirectByteBuffer(1L, 0));
        return getFieldOffsetInt(bb, -1);
    }

    public static long getPositionOffset() {
        ByteBuffer bb = Objects.requireNonNull(NewDirectByteBuffer(-1L, MAGIC_CAPACITY));
        bb.position(MAGIC_POSITION);
        return getFieldOffsetInt(bb, MAGIC_POSITION);
    }

    public static long getLimitOffset() {
        ByteBuffer bb = Objects.requireNonNull(NewDirectByteBuffer(-1L, MAGIC_CAPACITY));
        bb.limit(MAGIC_POSITION);
        return getFieldOffsetInt(bb, MAGIC_POSITION);
    }

    public static long getCapacityOffset() {
        ByteBuffer bb = Objects.requireNonNull(NewDirectByteBuffer(-1L, MAGIC_CAPACITY));
        bb.limit(0);
        return getFieldOffsetInt(bb, MAGIC_CAPACITY);
    }

    public static long getFieldOffset(Class<?> containerType, Class<?> fieldType, LongPredicate predicate) {
        Class<?> c = containerType;
        while (c != Object.class) {
            Field[] fields = c.getDeclaredFields();
            for (Field field : fields) {
                if (!field.getType().isAssignableFrom(fieldType) || Modifier.isStatic(field.getModifiers()) || field.isSynthetic()) {
                    continue;
                }

                long offset = UNSAFE.objectFieldOffset(field);
                if (predicate.test(offset)) {
                    return offset;
                }
            }
            c = c.getSuperclass();
        }
        throw new UnsupportedOperationException("Failed to find field offset in class.");
    }

    public static long getFieldOffsetObject(Object container, Object value) {
        return getFieldOffset(container.getClass(), value.getClass(), offset -> UNSAFE.getObject(container, offset) == value);
    }

    public static long getAddressOffset() {
        long MAGIC_ADDRESS = 0xDEADBEEF8BADF00DL & (BITS32 ? 0xFFFF_FFFFL : 0xFFFF_FFFF_FFFF_FFFFL);

        ByteBuffer bb = Objects.requireNonNull(NewDirectByteBuffer(MAGIC_ADDRESS, 0));

        return getFieldOffset(bb.getClass(), long.class, offset -> UNSAFE.getLong(bb, offset) == MAGIC_ADDRESS);
    }
}
