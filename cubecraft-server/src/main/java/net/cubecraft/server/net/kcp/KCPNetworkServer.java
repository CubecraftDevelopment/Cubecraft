package net.cubecraft.server.net.kcp;

import io.netty.buffer.ByteBuf;
import net.cubecraft.server.net.base.NetworkServer;
import net.cubecraft.server.net.base.ServerListener;

import java.net.InetSocketAddress;
import java.util.HashMap;

public final class KCPNetworkServer extends NetworkServer {
    private final KCPServerImpl serverImpl = new KCPServerImpl(this);
    private final HashMap<ServerListener, KCPServerListenerAdapter> listeners = new HashMap<>(32);
    private InetSocketAddress address;

    @Override
    public void stop() {
        this.serverImpl.close();
    }

    @Override
    public void start(InetSocketAddress address) {
        this.injectPacketProcessor();
        this.serverImpl.start(address);
        this.address = address;
    }

    @Override
    public void send(InetSocketAddress address, ByteBuf message) {
        this.serverImpl.send(address, message.resetReaderIndex());
    }

    @Override
    public void addListener(ServerListener listener) {
        if (this.listeners.containsKey(listener)) {
            return;
        }
        KCPServerListenerAdapter listenerAdapter = new KCPServerListenerAdapter(listener, this.serverImpl);
        this.serverImpl.addListener(listenerAdapter);
        this.listeners.put(listener, listenerAdapter);
    }

    @Override
    public void removeListener(ServerListener listener) {
        if (!this.listeners.containsKey(listener)) {
            return;
        }
        this.serverImpl.removeListener(this.listeners.get(listener));
        this.listeners.remove(listener);
    }

    @Override
    public InetSocketAddress getServerAddress() {
        return this.address;
    }

    @Override
    public void disconnect(InetSocketAddress address) {
        this.serverImpl.disconnect(address);
    }

    @Override
    public void broadcast(ByteBuf message) {
        for (KCPServerContext peer:this.serverImpl.getPeers()){
            peer.send(message);
        }
    }
}
