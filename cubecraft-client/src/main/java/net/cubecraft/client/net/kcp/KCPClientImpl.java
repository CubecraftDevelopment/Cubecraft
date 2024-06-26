package net.cubecraft.client.net.kcp;

import io.netty.buffer.ByteBuf;
import org.beykery.jkcp.KcpClient;
import org.beykery.jkcp.KcpListerner;
import org.beykery.jkcp.KcpOnUdp;

import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class KCPClientImpl extends KcpClient {
    private final KCPNetworkClient client;
    private final ExecutorService executor = Executors.newFixedThreadPool(4);
    private final Set<KCPClientListenerAdapter> listeners = new HashSet<>();

    public KCPClientImpl(KCPNetworkClient client) {
        this.client = client;
    }

    @Override
    public void connect(InetSocketAddress addr) {
        super.connect(addr);
        this.start();
        for (KCPClientListenerAdapter listener : this.getListeners()) {
            this.executor.submit(() -> listener.onConnect(this.client.getContext()));
        }
    }

    @Override
    public void handleReceive(ByteBuf bb, KcpOnUdp kcp) {
        for (KcpListerner listener : this.getListeners()) {
            listener.handleReceive(bb, kcp);
        }
    }



    @Override
    public void handleException(Throwable ex, KcpOnUdp kcp) {
        for (KcpListerner listener : this.getListeners()) {
            this.executor.submit(() -> listener.handleException(ex, kcp));
        }
    }

    public void addListener(KCPClientListenerAdapter listener) {
        this.listeners.add(listener);
    }

    public void removeListener(KCPClientListenerAdapter listener) {
        this.listeners.remove(listener);
    }

    public Set<KCPClientListenerAdapter> getListeners() {
        return listeners;
    }
}
