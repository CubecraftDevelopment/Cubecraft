package net.cubecraft.util;

public interface ArrayCodec {
    static byte[] encode(short[] shorts) {
        byte[] bytes = new byte[shorts.length * 2]; // 每个 short 2 个字节
        for (int i = 0; i < shorts.length; i++) {
            bytes[i * 2] = (byte) (shorts[i] & 0xFF);         // 低字节
            bytes[i * 2 + 1] = (byte) ((shorts[i] >> 8) & 0xFF); // 高字节
        }
        return bytes;
    }

    static short[] decodeS(byte[] bytes) {
        short[] shorts = new short[bytes.length / 2]; // 每个 short 2 个字节
        for (int i = 0; i < shorts.length; i++) {
            // 使用移位和按位与重构 short
            shorts[i] = (short) ((bytes[i * 2] & 0xFF) | ((bytes[i * 2 + 1] & 0xFF) << 8));
        }
        return shorts;
    }

    static byte[] encode(long value) {
        byte[] bytes = new byte[8]; // long 占用 8 个字节
        for (int i = 0; i < 8; i++) {
            bytes[i] = (byte) ((value >> (i * 8)) & 0xFF); // 提取每个字节
        }
        return bytes;
    }

    static long decodeL(byte[] bytes) {
        long value = 0;
        for (int i = 0; i < 8; i++) {
            value |= ((long) (bytes[i] & 0xFF)) << (i * 8); // 重构 long 值
        }
        return value;
    }
}
