package io.flybird.cubecraft.net.packet;


import io.netty.buffer.ByteBuf;

public interface Packet extends Event {

    void writePacketData(ByteBuf buffer) throws Exception;

    void readPacketData(ByteBuf buffer) throws Exception;
}
