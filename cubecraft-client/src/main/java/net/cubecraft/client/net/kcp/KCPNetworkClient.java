package net.cubecraft.client.net.kcp;

import io.netty.buffer.ByteBuf;
import net.cubecraft.client.net.base.ClientContext;
import net.cubecraft.client.net.base.ClientListener;
import net.cubecraft.client.net.base.NetworkClient;

import java.net.InetSocketAddress;
import java.util.HashMap;

public final class KCPNetworkClient extends NetworkClient {
    private final KCPClientImpl clientImpl = new KCPClientImpl(this);
    private final ClientContext context = new KCPClientContext(this);
    private final HashMap<ClientListener, KCPClientListenerAdapter> listeners = new HashMap<>(32);
    private InetSocketAddress address;

    @Override
    public void connect(InetSocketAddress address) {
        this.injectPacketProcessor();
        this.clientImpl.connect(address);
        this.address = address;
    }

    @Override
    public void disconnect() {
        this.clientImpl.close();
    }

    @Override
    public void send(ByteBuf message) {
        this.clientImpl.send(message.resetReaderIndex());
    }

    @Override
    public void addListener(ClientListener listener) {
        if (this.listeners.containsKey(listener)) {
            return;
        }
        KCPClientListenerAdapter listenerAdapter = new KCPClientListenerAdapter(listener, this.context);
        this.clientImpl.addListener(listenerAdapter);
        this.listeners.put(listener, listenerAdapter);
    }

    @Override
    public void removeListener(ClientListener listener) {
        if (!this.listeners.containsKey(listener)) {
            return;
        }
        this.clientImpl.removeListener(this.listeners.get(listener));
        this.listeners.remove(listener);
    }

    @Override
    public InetSocketAddress getServerAddress() {
        return this.address;
    }

    public ClientContext getContext() {
        return this.context;
    }
}
