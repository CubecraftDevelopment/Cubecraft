package io.flybird.cubecraft.net.packet;

import io.netty.buffer.ByteBuf;

public interface Packet{

    void writePacketData(ByteBuf buffer) throws Exception;

    void readPacketData(ByteBuf buffer) throws Exception;
}
