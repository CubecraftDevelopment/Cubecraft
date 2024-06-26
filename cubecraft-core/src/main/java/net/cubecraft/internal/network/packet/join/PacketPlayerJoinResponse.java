package net.cubecraft.internal.network.packet.join;

import net.cubecraft.util.ByteBufUtil;
import net.cubecraft.net.packet.Packet;
import net.cubecraft.net.packet.PacketConstructor;
import me.gb2022.commons.registry.TypeItem;
import io.netty.buffer.ByteBuf;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * a msg packet with no data carried
 */
@TypeItem("cubecraft:join_response")
public class PacketPlayerJoinResponse implements Packet {
    public static final String STATUS_ACCEPTED = "__ACCEPT__";

    private String reason;

    public PacketPlayerJoinResponse(String reason) {
        this.reason = reason;
    }

    @PacketConstructor
    public PacketPlayerJoinResponse() {
    }

    public static PacketPlayerJoinResponse accept() {
        return new PacketPlayerJoinResponse(STATUS_ACCEPTED);
    }

    @Override
    public void writePacketData(ByteBuf buffer) {
        buffer.writeBytes(this.reason.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public void readPacketData(ByteBuf buffer) {
        this.reason = new String(ByteBufUtil.unwrap(buffer), StandardCharsets.UTF_8);
    }

    public boolean isAccepted() {
        return Objects.equals(reason, STATUS_ACCEPTED);
    }

    public String getReason() {
        return reason;
    }
}
