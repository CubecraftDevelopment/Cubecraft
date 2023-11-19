package net.cubecraft.net;

import net.cubecraft.net.packet.Packet;

import java.net.InetSocketAddress;

public record NetHandlerContext(InetSocketAddress from, NetworkListenerAdapter handler) {

    public void sendPacket(Packet pkt){
        this.handler.sendPacket(pkt,from);
    }

    public void closeConnection(){
        this.handler.disconnect(from);
    }

    public void runTask(Runnable command){
        this.handler.runTask(command);
    }
}
