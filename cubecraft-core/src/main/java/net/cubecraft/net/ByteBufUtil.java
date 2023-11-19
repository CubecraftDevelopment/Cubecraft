package net.cubecraft.net;

import ink.flybird.fcommon.nbt.NBT;
import ink.flybird.fcommon.nbt.NBTBase;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public interface ByteBufUtil {
    /**
     * build buffer from nbt.
     * @param tag tag
     * @return build buffer
     */
    static ByteBuf fromNBT(NBTBase tag) {
        ByteBuf byteBuf = ByteBufAllocator.DEFAULT.ioBuffer();
        try {
            ByteBufOutputStream stream=new ByteBufOutputStream(byteBuf);
            NBT.write(tag,stream);
            stream.close();
        } catch (Exception e) {
            throw new RuntimeException("could not write NBT:" + e);
        }
        return byteBuf;
    }

    /**
     * build nbt tag from bytebuffer.
     * @param byteBuf source
     * @return nbt
     */
    static NBTBase toNBT(ByteBuf byteBuf) {
        NBTBase base;
        try {
            ByteBufInputStream stream=new ByteBufInputStream(byteBuf);
            base= NBT.read(stream);
            stream.close();
        } catch (Exception e) {
            throw new RuntimeException("could not read nbt:" + e);
        }
        return base;
    }

    /**
     * wrap some data to buffer.
     * @param arr array.
     * @return wrapped buffer.
     */
    static ByteBuf wrap(byte[] arr) {
        ByteBuf byteBuf = ByteBufAllocator.DEFAULT.ioBuffer(arr.length);
        try {
            ByteBufOutputStream stream=new ByteBufOutputStream(byteBuf);
            stream.write(arr);
            stream.close();
        } catch (IOException e) {
            throw new RuntimeException("could not wrap byte buf:" + e);
        }
        return byteBuf;
    }

    /**
     * free a fucking java.nio.buffer
     * @param buffer buffer
     */
    static byte[] unwrap(ByteBuf buffer){
        byte[] data=new byte[buffer.writerIndex()-buffer.readerIndex()];
        buffer.readBytes(data);
        return data;
    }

    /**
     * read a string from buffer with metadata length,friendly-reading.
     * @param buffer target
     * @return string
     */
    static String readString(ByteBuf buffer) {
        int len=buffer.readByte();
        return (String) buffer.readCharSequence(len, StandardCharsets.UTF_8);
    }

    /**
     * write a string to buffer with metadata length,friendly-reading.
     * @param buffer target
     */
    static void writeString(String s,ByteBuf buffer) {
        buffer.writeByte(s.getBytes(StandardCharsets.UTF_8).length);
        buffer.writeBytes(s.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * read an array to buffer with metadata length,friendly-reading.
     * @param buffer target
     */
    static void writeArray(byte[] arr,ByteBuf buffer) {
        buffer.writeInt(arr.length);
        buffer.writeBytes(arr);
    }

    static byte[] readArray(ByteBuf buffer) {
        byte[] arr=new byte[buffer.readInt()];
        buffer.readBytes(arr);
        return arr;
    }
}
