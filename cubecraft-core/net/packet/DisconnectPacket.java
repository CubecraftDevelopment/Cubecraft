package io.flybird.cubecraft.net.packet;



import java.net.InetSocketAddress;

public record DisconnectPacket(InetSocketAddress address,String reason) {}
