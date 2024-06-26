package net.cubecraft.server.net.kcp;

import io.netty.buffer.ByteBuf;
import net.cubecraft.server.net.base.ServerListener;
import net.cubecraft.server.net.base.ServerListenerAdapter;
import org.beykery.jkcp.KcpListerner;
import org.beykery.jkcp.KcpOnUdp;

public final class KCPServerListenerAdapter extends ServerListenerAdapter implements KcpListerner {
    private final KCPServerImpl server;

    public KCPServerListenerAdapter(ServerListener listener, KCPServerImpl server) {
        super(listener);
        this.server = server;
    }

    @Override
    public void handleReceive(ByteBuf bb, KcpOnUdp kcp) {
        this.listener.handleMessage(bb, this.getContext(kcp));
    }

    @Override
    public void handleException(Throwable ex, KcpOnUdp kcp) {
        this.listener.handleException(ex, this.getContext(kcp));
    }

    @Override
    public void handleClose(KcpOnUdp kcp) {
        this.listener.onDisconnect(this.getContext(kcp));
    }

    public KCPServerContext getContext(KcpOnUdp kcp) {
        return this.server.getPeer(kcp.getRemote());
    }
}
