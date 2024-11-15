package me.gb2022.quantum3d.util;



import java.nio.*;

/**
 * simple util for buffer
 *
 * @author GrassBlock2022
 */
public interface BufferUtil {

    /**
     * create a buffer from values
     * we do not recommend you to use this method due to buffer is un collectable and will cause memory leak.
     *
     * @param values values
     * @return buffer
     */
    static ByteBuffer from(byte... values) {
        ByteBuffer Bb = BufferAllocation.allocByteBuffer(values.length);
        Bb.clear();
        Bb.put(values);
        Bb.flip();
        return Bb;
    }

    /**
     * create a buffer from values
     * we do not recommend you to use this method due to buffer is un collectable and will cause memory leak.
     *
     * @param values values
     * @return buffer
     */
    static ShortBuffer from(short... values) {
        ShortBuffer Sb = BufferAllocation.allocShortBuffer(values.length);
        Sb.clear();
        Sb.put(values);
        Sb.flip();
        return Sb;
    }

    /**
     * create a buffer from values
     * we do not recommend you to use this method due to buffer is un collectable and will cause memory leak.
     *
     * @param values values
     * @return buffer
     */
    static IntBuffer from(int... values) {
        IntBuffer Ib = BufferAllocation.allocIntBuffer(values.length);
        Ib.clear();
        Ib.put(values);
        Ib.flip();
        return Ib;
    }

    /**
     * create a buffer from values
     * we do not recommend you to use this method due to buffer is un collectable and will cause memory leak.
     *
     * @param values values
     * @return buffer
     */
    static LongBuffer from(long... values) {
        LongBuffer Lb = BufferAllocation.allocLongBuffer(values.length);
        Lb.clear();
        Lb.put(values);
        Lb.flip();
        return Lb;
    }

    /**
     * create a buffer from values
     * we do not recommend you to use this method due to buffer is un collectable and will cause memory leak.
     *
     * @param values values
     * @return buffer
     */
    static FloatBuffer from(float... values) {
        FloatBuffer Fb = BufferAllocation.allocFloatBuffer(values.length);
        Fb.clear();
        Fb.put(values);
        Fb.flip();
        return Fb;
    }

    /**
     * create a buffer from values
     * we do not recommend you to use this method due to buffer is un collectable and will cause memory leak.
     *
     * @param values values
     * @return buffer
     */
    static DoubleBuffer from(double... values) {
        DoubleBuffer Db = BufferAllocation.allocDoubleBuffer(values.length);
        Db.clear();
        Db.put(values);
        Db.flip();
        return Db;
    }

    /**
     * clear,fill an existing buffer with a set of values,and flip it
     * @param buffer buffer to fill
     * @param values values
     */
    static void fillBuffer(DoubleBuffer buffer, double... values) {
        buffer.clear();
        buffer.put(values);
        buffer.flip();
    }

    /**
     * clear,fill an existing buffer with a set of values,and flip it
     * @param buffer buffer to fill
     * @param values values
     */
    static void fillBuffer(FloatBuffer buffer, float... values) {
        buffer.clear();
        buffer.put(values);
        buffer.flip();
    }

    /**
     * clear,fill an existing buffer with a set of values,and flip it
     * @param buffer buffer to fill
     * @param values values
     */
    static void fillBuffer(ShortBuffer buffer, short... values) {
        buffer.clear();
        buffer.put(values);
        buffer.flip();
    }

    /**
     * clear,fill an existing buffer with a set of values,and flip it
     * @param buffer buffer to fill
     * @param values values
     */
    static void fillBuffer(ByteBuffer buffer, byte... values) {
        buffer.clear();
        buffer.put(values);
        buffer.flip();
    }

    /**
     * clear,fill an existing buffer with a set of values,and flip it
     * @param buffer buffer to fill
     * @param values values
     */
    static void fillBuffer(IntBuffer buffer, int... values) {
        buffer.clear();
        buffer.put(values);
        buffer.flip();
    }

    /**
     * clear,fill an existing buffer with a set of values,and flip it
     * @param buffer buffer to fill
     * @param values values
     */
    static void fillBuffer(CharBuffer buffer, char... values) {
        buffer.clear();
        buffer.put(values);
        buffer.flip();
    }

    /**
     * create buffer from existing buffer,convert to another datatype value.
     * remember:buffer is not collectable and will cause memory leaking.
     *
     * @param buffer existing buffer
     * @return new buffer
     */
    static FloatBuffer double2float(DoubleBuffer buffer) {
        float[] data = new float[buffer.capacity()];
        for (int i = 0; i < buffer.capacity(); i++) {
            data[i] = (float) buffer.get(i);
        }
        return from(data);
    }

    /**
     * create buffer from existing buffer,convert to another datatype value.
     * remember:buffer is not collectable and will cause memory leaking.
     *
     * @param buffer existing buffer
     * @return new buffer
     */
    static IntBuffer long2Int(LongBuffer buffer) {
        int[] data = new int[buffer.capacity()];
        for (int i = 0; i < buffer.capacity(); i++) {
            data[i] = (int) buffer.get(i);
        }
        return from(data);
    }

    /**
     * create buffer from existing buffer,convert to another datatype value.
     * remember:buffer is not collectable and will cause memory leaking.
     *
     * @param buffer existing buffer
     * @return new buffer
     */
    static ShortBuffer int2Short(IntBuffer buffer) {
        short[] data = new short[buffer.capacity()];
        for (int i = 0; i < buffer.capacity(); i++) {
            data[i] = (short) buffer.get(i);
        }
        return from(data);
    }

    /**
     * create buffer from existing buffer,convert to another datatype value.
     * remember:buffer is not collectable and will cause memory leaking.
     *
     * @param buffer existing buffer
     * @return new buffer
     */
    static ByteBuffer short2Byte(ShortBuffer buffer) {
        byte[] data = new byte[buffer.capacity()];
        for (int i = 0; i < buffer.capacity(); i++) {
            data[i] = (byte) buffer.get(i);
        }
        return from(data);
    }

    /**
     * create buffer from existing buffer,convert to another datatype value.
     * remember:buffer is not collectable and will cause memory leaking.
     *
     * @param buffer existing buffer
     * @return new buffer
     */
    static DoubleBuffer float2Double(FloatBuffer buffer) {
        double[] data = new double[buffer.capacity()];
        for (int i = 0; i < buffer.capacity(); i++) {
            data[i] = buffer.get(i);
        }
        return from(data);
    }

    /**
     * create buffer from existing buffer,convert to another datatype value.
     * remember:buffer is not collectable and will cause memory leaking.
     *
     * @param buffer existing buffer
     * @return new buffer
     */
    static LongBuffer int2Long(IntBuffer buffer) {
        long[] data = new long[buffer.capacity()];
        for (int i = 0; i < buffer.capacity(); i++) {
            data[i] = buffer.get(i);
        }
        return from(data);
    }

    /**
     * create buffer from existing buffer,convert to another datatype value.
     * remember:buffer is not collectable and will cause memory leaking.
     *
     * @param buffer existing buffer
     * @return new buffer
     */
    static IntBuffer short2Int(ShortBuffer buffer) {
        int[] data = new int[buffer.capacity()];
        for (int i = 0; i < buffer.capacity(); i++) {
            data[i] = buffer.get(i);
        }
        return from(data);
    }

    /**
     * create buffer from existing buffer,convert to another datatype value.
     * remember:buffer is not collectable and will cause memory leaking.
     *
     * @param buffer existing buffer
     * @return new buffer
     */
    static ShortBuffer byte2Short(ByteBuffer buffer) {
        short[] data = new short[buffer.capacity()];
        for (int i = 0; i < buffer.capacity(); i++) {
            data[i] = buffer.get(i);
        }
        return from(data);
    }
}
