package net.cubecraft.server.net.kcp;

import io.netty.buffer.ByteBuf;
import net.cubecraft.server.net.base.NetworkServer;
import net.cubecraft.server.net.base.ServerContext;
import org.beykery.jkcp.KcpOnUdp;

import java.net.InetSocketAddress;

public final class KCPServerContext implements ServerContext {
    private final KCPNetworkServer server;
    private final InetSocketAddress clientAddress;
    private final KcpOnUdp peer;

    public KCPServerContext(KCPNetworkServer server, KcpOnUdp kcp) {
        this.server = server;
        this.clientAddress = kcp.getRemote();
        this.peer = kcp;
    }

    @Override
    public void send(ByteBuf message) {
        this.peer.send(message);
    }

    @Override
    public void disconnect() {
        this.peer.close();
    }

    @Override
    public NetworkServer getServer() {
        return this.server;
    }

    @Override
    public InetSocketAddress getClientAddress() {
        return clientAddress;
    }

    public KcpOnUdp getPeer() {
        return this.peer;
    }
}
