package net.cubecraft.server.net;

import com.whirvis.jraknet.RakNetException;
import com.whirvis.jraknet.server.RakNetServer;
import net.cubecraft.net.packet.Packet;
import net.cubecraft.SharedContext;
import net.cubecraft.server.ServerSharedContext;

import java.net.InetSocketAddress;


public class RakNetServerIO implements ServerIO {
    private final RakNetServerListenerAdapter listener;
    private RakNetServer server;

    public RakNetServerIO() {
        this.listener = new RakNetServerListenerAdapter(SharedContext.PACKET, ServerSharedContext.NET_HANDLER);
    }

    public RakNetServer getServer() {
        return server;
    }

    @Override
    public void start(int port, int maxConn) {
        new Thread(() -> {
            server = new RakNetServer(port, maxConn);
            server.addListener(listener);
            try {
                server.start();
            } catch (RakNetException e) {
                throw new RuntimeException(e);
            }
        }).start();

    }

    @Override
    public ServerListenerAdapter getListener() {
        return listener;
    }

    @Override
    public void sendPacket(Packet pkt, InetSocketAddress address) {
        this.getListener().sendPacket(pkt, address);
    }

    @Override
    public void closeConnection(InetSocketAddress address) {
        this.getListener().disconnect(address);
    }

    @Override
    public void broadcastPacket(Packet pkt) {
        this.getListener().broadCastPacket(pkt);
    }

    @Override
    public void allCloseConnection() {
        this.getListener().allCloseConnection();
    }

    @Override
    public void stop() {
        this.server.shutdown();
    }
}
