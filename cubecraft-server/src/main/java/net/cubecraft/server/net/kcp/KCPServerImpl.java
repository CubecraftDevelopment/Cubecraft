package net.cubecraft.server.net.kcp;

import io.netty.buffer.ByteBuf;
import org.beykery.jkcp.KcpListerner;
import org.beykery.jkcp.KcpOnUdp;
import org.beykery.jkcp.KcpServer;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class KCPServerImpl {
    private final ExecutorService executor = Executors.newFixedThreadPool(4);
    private final Set<KCPServerListenerAdapter> listeners = new HashSet<>();
    private final HashMap<InetSocketAddress, KCPServerContext> peers = new HashMap<>();
    private final KCPNetworkServer server;
    private KcpServerWrapper wrapper = null;

    public KCPServerImpl(KCPNetworkServer server) {
        this.server = server;
    }

    public void start(InetSocketAddress addr) {
        if (this.wrapper != null) {
            this.wrapper.close();
        }
        this.wrapper = new KcpServerWrapper(addr, this);
        this.wrapper.start();
    }

    public void handleReceive(ByteBuf bb, KcpOnUdp kcp) {
        if (!this.peers.containsKey(kcp.getRemote())) {
            this.peers.put(kcp.getRemote(), new KCPServerContext(this.server, kcp));
        }

        for (KcpListerner listener : this.getListeners()) {
            listener.handleReceive(bb, kcp);
        }
    }

    public void handleException(Throwable ex, KcpOnUdp kcp) {
        for (KcpListerner listener : this.getListeners()) {
            this.executor.submit(() -> listener.handleException(ex, kcp));
        }
    }

    public void handleClose(KcpOnUdp kcp) {
        for (KcpListerner listener : this.getListeners()) {
            this.executor.submit(() -> listener.handleClose(kcp));
        }
    }

    public void addListener(KCPServerListenerAdapter listener) {
        this.listeners.add(listener);
    }

    public void removeListener(KCPServerListenerAdapter listener) {
        this.listeners.remove(listener);
    }

    public Set<KCPServerListenerAdapter> getListeners() {
        return listeners;
    }

    public void close() {
        this.wrapper.close();
        this.wrapper = null;
    }

    public void disconnect(InetSocketAddress address) {
        this.peers.get(address).getPeer().close();
    }

    public KcpServerWrapper getWrapper() {
        return wrapper;
    }

    public KCPServerContext getPeer(InetSocketAddress address) {
        return this.peers.get(address);
    }

    public void send(InetSocketAddress address, ByteBuf message) {
        this.wrapper.send(message, this.getPeer(address).getPeer());
    }

    public Collection<KCPServerContext> getPeers() {
        return this.peers.values();
    }

    public KCPNetworkServer getParent() {
        return this.server;
    }

    public static final class KcpServerWrapper extends KcpServer {
        private final KCPServerImpl impl;

        public KcpServerWrapper(InetSocketAddress address, KCPServerImpl impl) {
            super(address.getPort(), 8);
            this.impl = impl;
        }

        @Override
        public void handleReceive(ByteBuf bb, KcpOnUdp kcp) {
            this.impl.handleReceive(bb, kcp);
        }

        @Override
        public void handleException(Throwable ex, KcpOnUdp kcp) {
            this.impl.handleException(ex, kcp);
        }

        @Override
        public void handleClose(KcpOnUdp kcp) {
            this.impl.handleClose(kcp);
        }
    }
}
