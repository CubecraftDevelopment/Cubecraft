package net.cubecraft.util;

import me.gb2022.commons.nbt.NBT;
import me.gb2022.commons.nbt.NBTBase;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public interface ByteBufUtil {


    /**
     * wrap some data to buffer.
     *
     * @param arr array.
     * @return wrapped buffer.
     */
    static ByteBuf wrap(byte[] arr) {
        ByteBuf byteBuf = ByteBufAllocator.DEFAULT.ioBuffer(arr.length);
        try {
            ByteBufOutputStream stream = new ByteBufOutputStream(byteBuf);
            stream.write(arr);
            stream.close();
        } catch (IOException e) {
            throw new RuntimeException("could not wrap byte buf:" + e);
        }
        return byteBuf;
    }

    /**
     * free a fucking java.nio.buffer
     *
     * @param buffer buffer
     */
    static byte[] unwrap(ByteBuf buffer) {
        byte[] data = new byte[buffer.writerIndex() - buffer.readerIndex()];
        buffer.readBytes(data);
        return data;
    }

    /**
     * read a string from buffer with metadata length,friendly-reading.
     *
     * @param buffer target
     * @return string
     */
    static String readString(ByteBuf buffer) {
        int len = buffer.readByte();
        return (String) buffer.readCharSequence(len, StandardCharsets.UTF_8);
    }

    /**
     * write a string to buffer with metadata length,friendly-reading.
     *
     * @param buffer target
     */
    static void writeString(String s, ByteBuf buffer) {
        buffer.writeByte(s.getBytes(StandardCharsets.UTF_8).length);
        buffer.writeBytes(s.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * read an array to buffer with metadata length,friendly-reading.
     *
     * @param buffer target
     */
    static void writeArray(byte[] arr, ByteBuf buffer) {
        buffer.writeInt(arr.length);
        buffer.writeBytes(arr);
    }

    static byte[] readArray(ByteBuf buffer) {
        byte[] arr = new byte[buffer.readInt()];
        buffer.readBytes(arr);
        return arr;
    }
}
