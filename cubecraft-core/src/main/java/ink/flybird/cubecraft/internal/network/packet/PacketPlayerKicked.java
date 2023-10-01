package ink.flybird.cubecraft.internal.network.packet;

import ink.flybird.cubecraft.net.packet.Packet;
import ink.flybird.cubecraft.net.packet.PacketConstructor;
import ink.flybird.fcommon.registry.TypeItem;
import io.netty.buffer.ByteBuf;

import java.nio.charset.StandardCharsets;

@TypeItem("cubecraft:player_kicked")
public class PacketPlayerKicked implements Packet {
    private String reason;

    public PacketPlayerKicked(String reason) {
        this.reason = reason;
    }

    @PacketConstructor
    public PacketPlayerKicked(){}

    @Override
    public void writePacketData(ByteBuf buffer) {
        buffer.writeBytes(this.reason.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public void readPacketData(ByteBuf buffer) {
        this.reason=new String(buffer.array(), StandardCharsets.UTF_8);
    }

    public String getReason() {
        return reason;
    }
}