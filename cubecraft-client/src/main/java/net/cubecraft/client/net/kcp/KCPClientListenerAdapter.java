package net.cubecraft.client.net.kcp;

import io.netty.buffer.ByteBuf;
import net.cubecraft.client.net.base.ClientContext;
import net.cubecraft.client.net.base.ClientListener;
import net.cubecraft.client.net.base.ClientListenerAdapter;
import org.beykery.jkcp.KcpListerner;
import org.beykery.jkcp.KcpOnUdp;

public final class KCPClientListenerAdapter extends ClientListenerAdapter implements KcpListerner {
    public KCPClientListenerAdapter(ClientListener listener, ClientContext context) {
        super(listener, context);
    }

    void onConnect(ClientContext context) {
        this.listener.onConnect(context);
    }

    @Override
    public void handleReceive(ByteBuf bb, KcpOnUdp kcp) {
        this.listener.handleMessage(bb, this.context);
    }

    @Override
    public void handleException(Throwable ex, KcpOnUdp kcp) {
        this.listener.handleException(ex, this.context);
    }

    @Override
    public void handleClose(KcpOnUdp kcp) {
        this.listener.onDisconnect(this.context);
    }
}
