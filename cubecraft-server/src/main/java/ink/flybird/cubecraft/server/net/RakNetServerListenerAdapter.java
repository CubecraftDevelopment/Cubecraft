package ink.flybird.cubecraft.server.net;

import com.whirvis.jraknet.RakNetPacket;
import com.whirvis.jraknet.identifier.Identifier;
import com.whirvis.jraknet.peer.RakNetClientPeer;
import com.whirvis.jraknet.protocol.ConnectionType;
import com.whirvis.jraknet.protocol.Reliability;
import com.whirvis.jraknet.protocol.message.CustomPacket;
import com.whirvis.jraknet.server.RakNetServer;
import com.whirvis.jraknet.server.RakNetServerListener;
import com.whirvis.jraknet.server.ServerPing;
import ink.flybird.cubecraft.net.NetHandlerContext;
import ink.flybird.cubecraft.net.NetworkListenerAdapter;
import ink.flybird.cubecraft.net.packet.*;
import ink.flybird.cubecraft.server.net.ServerListenerAdapter;
import ink.flybird.cubecraft.server.net.ServerNetHandler;
import ink.flybird.fcommon.registry.ConstructingMap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Properties;

/**
 * an implementation of {@link NetworkListenerAdapter}.
 *
 * @author GrassBlock2022
 */
public class RakNetServerListenerAdapter extends ServerListenerAdapter implements RakNetServerListener {
    private final HashMap<InetSocketAddress, RakNetClientPeer> clients = new HashMap<>();
    private RakNetServer server;

    /**
     * an adapter of handler.
     *
     * @param packetConstructor packet registry.
     * @param handlerRegistry   handler registry.
     */
    public RakNetServerListenerAdapter(ConstructingMap<Packet> packetConstructor, ConstructingMap<ServerNetHandler> handlerRegistry) {
        super(packetConstructor, handlerRegistry);
    }


    //implementation of RakNet listener
    /**
     * {@inheritDoc}
     *
     * @param server         the server.
     * @param address        the address of the client.
     * @param connectionType the connection type of the client.
     */
    @Override
    public void onConnect(RakNetServer server, InetSocketAddress address, ConnectionType connectionType) {
        this.callEvent(new ConnectPacket(address), new NetHandlerContext(address, this));
    }

    /**
     * {@inheritDoc}
     *
     * @param server the server.
     * @param peer   the client that logged in.
     */
    @Override
    public void onLogin(RakNetServer server, RakNetClientPeer peer) {
        this.clients.put(peer.getAddress(), peer);
        this.callEvent(new ConnectSuccessPacket(peer.getAddress()), new NetHandlerContext(peer.getAddress(), this));
    }

    /**
     * {@inheritDoc}
     *
     * @param server  the server.
     * @param address the address of the client that disconnected.
     * @param peer    the client that disconnected, this will be <code>null</code>
     *                if the client has not yet logged in.
     * @param reason  the reason the client disconnected.
     */
    @Override
    public void onDisconnect(RakNetServer server, InetSocketAddress address, RakNetClientPeer peer, String reason) {
        this.callEvent(new DisconnectPacket(address, reason), new NetHandlerContext(address, this));
        this.clients.remove(address);
    }

    /**
     * {@inheritDoc}
     *
     * @param server  the server.
     * @param peer    the client that sent the packet.
     * @param packet  the packet received from the client.
     * @param channel the channel the packet was sent on.
     */
    @Override
    public void handleMessage(RakNetServer server, RakNetClientPeer peer, RakNetPacket packet, int channel) {
        Packet pkt=this.decode(packet.buffer().readerIndex(0));
        this.callEvent(pkt, new NetHandlerContext(peer.getAddress(), this));
    }



    /**
     * {@inheritDoc}
     *
     * @param server the server.
     * @param ping   the response that will be sent to the client.
     */
    @Override
    public void onPing(RakNetServer server, ServerPing ping) {
        Properties p=new Properties();
        this.callEvent(new PingPacket(p), null);
        ping.setIdentifier(new Identifier(p.toString()));
    }


    //implementation of abstract handler
    /**
     * {@inheritDoc}
     *
     * @param pkt     packet
     * @param address target
     */
    @Override
    public void sendPacket(Packet pkt, InetSocketAddress address) {
        RakNetClientPeer peer = this.clients.get(address);
        if (peer == null) {
            throw new RuntimeException("no connection for address:" + address.toString());
        }
        ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer();
        this.encode(pkt, buffer);
        byte[] arr=new byte[buffer.writerIndex()];
        buffer.resetReaderIndex();
        buffer.readBytes(arr);
        buffer.release();
        //this.server.sendMessage(peer,Reliability.RELIABLE,new CustomPacket(new RakNetPacket(arr)));
    }

    /**
     * {@inheritDoc}
     *
     * @param address addr that you want to kick.
     */
    @Override
    public void disconnect(InetSocketAddress address) {
        RakNetClientPeer peer = this.clients.get(address);
        if (peer == null) {
            throw new RuntimeException("no connection for address:" + address.toString());
        }
        peer.disconnect();
        this.clients.remove(address);
    }

    @Override
    public void broadCastPacket(Packet pkt) {
        for (RakNetClientPeer peer:this.clients.values()){
            sendPacket(pkt,peer.getAddress());
        }
    }

    @Override
    public void allCloseConnection(){
        for (RakNetClientPeer peer:this.clients.values()){
            peer.disconnect();
        }
        this.clients.clear();
    }

    @Override
    public void onHandlerException(RakNetServer server, InetSocketAddress address, Throwable throwable) {
        throw new RuntimeException(throwable);
    }

    @Override
    public void onPeerException(RakNetServer server, RakNetClientPeer peer, Throwable throwable) {
        throw new RuntimeException(throwable);
    }

    @Override
    public void onStart(RakNetServer server) {
        this.server=server;
    }
}
