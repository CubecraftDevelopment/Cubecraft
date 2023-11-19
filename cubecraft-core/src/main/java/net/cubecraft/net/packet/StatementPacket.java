package net.cubecraft.net.packet;

import io.netty.buffer.ByteBuf;

/**
 * a statement or signal packet that carries no data.
 */
public abstract class StatementPacket implements Packet{
    @Override
    public void writePacketData(ByteBuf buffer){
        buffer.writeInt(0);
    }

    @Override
    public void readPacketData(ByteBuf buffer){
        buffer.readInt();
    }

    @PacketConstructor
    public StatementPacket() {
    }
}
