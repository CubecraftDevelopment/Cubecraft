package net.cubecraft.util;

import me.gb2022.commons.nbt.NBT;
import me.gb2022.commons.nbt.NBTBase;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public interface NBTBufferCodec {

    static void writeCompressedNBT(ByteBuf buffer, NBTBase tag) {
        try {
            ByteBufOutputStream stream = new ByteBufOutputStream(buffer);
            GZIPOutputStream zip = new GZIPOutputStream(stream);
            DataOutputStream data = new DataOutputStream(zip);
            NBT.write(tag, data);
            data.close();
            zip.close();
            stream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static <T extends NBTBase> T readCompressedNBT(ByteBuf buffer, Class<T> type) {
        try {
            ByteBufInputStream stream = new ByteBufInputStream(buffer);
            GZIPInputStream zip = new GZIPInputStream(stream);
            DataInputStream data = new DataInputStream(zip);
            NBTBase tag = NBT.read(data);
            data.close();
            zip.close();
            stream.close();
            return type.cast(tag);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static void writeNBT(ByteBuf buffer, NBTBase tag) {
        try {
            ByteBufOutputStream stream = new ByteBufOutputStream(buffer);
            DataOutputStream data = new DataOutputStream(stream);
            NBT.write(tag, data);
            data.close();
            stream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static <T extends NBTBase> T readNBT(ByteBuf buffer, Class<T> type) {
        try {
            ByteBufInputStream stream = new ByteBufInputStream(buffer);
            DataInputStream data = new DataInputStream(stream);
            NBTBase tag = NBT.read(data);
            data.close();
            stream.close();
            return type.cast(tag);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
