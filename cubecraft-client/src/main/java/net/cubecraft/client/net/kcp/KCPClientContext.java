package net.cubecraft.client.net.kcp;

import io.netty.buffer.ByteBuf;
import net.cubecraft.client.net.base.ClientContext;
import net.cubecraft.client.net.base.NetworkClient;

public final class KCPClientContext implements ClientContext {
    private final KCPNetworkClient client;

    public KCPClientContext(KCPNetworkClient client) {
        this.client = client;
    }

    @Override
    public void send(ByteBuf message) {
        this.client.send(message);
    }

    @Override
    public void disconnect() {
        this.client.disconnect();
    }

    @Override
    public NetworkClient getClient() {
        return this.client;
    }
}
