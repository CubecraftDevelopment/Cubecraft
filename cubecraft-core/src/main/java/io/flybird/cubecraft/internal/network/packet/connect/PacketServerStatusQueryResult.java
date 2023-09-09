package io.flybird.cubecraft.internal.network.packet.connect;


import io.flybird.cubecraft.net.packet.Packet;
import io.flybird.cubecraft.net.packet.PacketConstructor;
import io.netty.buffer.ByteBuf;

public class PacketServerStatusQueryResult implements Packet {
    private int status;

    public PacketServerStatusQueryResult(int status) {
        this.status = status;
    }

    @PacketConstructor
    public PacketServerStatusQueryResult(){}

    @Override
    public void writePacketData(ByteBuf buffer) throws Exception {
        buffer.writeInt(this.status);
    }

    @Override
    public void readPacketData(ByteBuf buffer) throws Exception {
        this.status=buffer.readInt();
    }

    public int getStatus() {
        return status;
    }
}
