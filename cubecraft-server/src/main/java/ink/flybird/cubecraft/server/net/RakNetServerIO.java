package ink.flybird.cubecraft.server.net;

import com.whirvis.jraknet.RakNetException;
import com.whirvis.jraknet.server.RakNetServer;
import ink.flybird.cubecraft.net.packet.Packet;
import ink.flybird.cubecraft.SharedContext;
import ink.flybird.cubecraft.server.ServerRegistries;

import java.net.InetSocketAddress;


public class RakNetServerIO implements ServerIO {
    private final ink.flybird.cubecraft.server.net.RakNetServerListenerAdapter listener;
    private RakNetServer server;

    public RakNetServerIO() {
        this.listener = new RakNetServerListenerAdapter(SharedContext.PACKET, ServerRegistries.NET_HANDLER);
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
}
