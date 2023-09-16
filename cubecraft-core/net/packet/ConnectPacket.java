package ink.flybird.cubecraft.net.packet;



import java.net.InetSocketAddress;

public record ConnectPacket(InetSocketAddress address) {
}
