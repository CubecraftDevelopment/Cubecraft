package net.cubecraft.net.packet;

import java.net.InetSocketAddress;

public record ConnectPacket(InetSocketAddress address){
}
