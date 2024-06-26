package net.cubecraft.net.packet;

import me.gb2022.commons.registry.TypeItem;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import net.cubecraft.SharedContext;

import java.nio.charset.StandardCharsets;

public interface Packet {
    int HEADER_LENGTH_LIMIT = 64;
    int PACKET_SIZE_LIMIT = 1024;
    byte PACKET_HEADER = 0xF;

    static void writePacketId(Packet packet, ByteBuf target) {
        byte[] head = packet.getPacketId().getBytes(StandardCharsets.US_ASCII);

        if (head.length > HEADER_LENGTH_LIMIT) {
            throw new RuntimeException("header too long!");
        }

        target.writeByte(head.length);
        target.writeBytes(head);
    }

    static String readPacketId(ByteBuf target) {
        byte headerLength = target.readByte();

        if (headerLength > HEADER_LENGTH_LIMIT) {
            throw new RuntimeException("header too long!");
        }

        byte[] head = new byte[headerLength];

        target.readBytes(head);

        return new String(head, StandardCharsets.US_ASCII);
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    static boolean isPacket(ByteBuf message) {
        int index = message.readerIndex();
        boolean result = message.readByte() == Packet.PACKET_HEADER;
        message.readerIndex(index);
        return result;
    }

    static ByteBuf encode(Packet packet) {
        ByteBuf buffer = ByteBufAllocator.DEFAULT.ioBuffer();
        buffer.resetWriterIndex();
        buffer.writeByte(PACKET_HEADER);

        ByteBuf data = ByteBufAllocator.DEFAULT.ioBuffer();
        try {
            packet.writePacketData(data);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        int len = data.writerIndex();

        if (len > PACKET_SIZE_LIMIT) {
            throw new RuntimeException("data too long!");
        }

        writePacketId(packet, buffer);

        buffer.writeShort(len);
        buffer.writeBytes(data);

        data.release();
        return buffer;
    }

    static Packet decode(ByteBuf buffer) {
        buffer.resetReaderIndex();
        buffer.readByte();
        String id = readPacketId(buffer);

        short len = buffer.readShort();

        if (len < 0) {
            throw new RuntimeException("invalid data size!");
        }

        ByteBuf data = ByteBufAllocator.DEFAULT.ioBuffer(len);
        buffer.readBytes(data, len);

        Packet packet = SharedContext.PACKET.create(id);

        try {
            packet.readPacketData(data);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        data.release();
        return packet;
    }


    void writePacketData(ByteBuf buffer) throws Exception;

    void readPacketData(ByteBuf buffer) throws Exception;

    default String getPacketId() {
        return this.getClass().getAnnotation(TypeItem.class).value();
    }
}
