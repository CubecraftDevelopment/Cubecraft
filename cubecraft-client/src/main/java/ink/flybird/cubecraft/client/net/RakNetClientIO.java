package ink.flybird.cubecraft.client.net;

import com.whirvis.jraknet.RakNetException;
import com.whirvis.jraknet.RakNetPacket;
import com.whirvis.jraknet.client.RakNetClient;
import com.whirvis.jraknet.client.RakNetClientListener;
import com.whirvis.jraknet.peer.RakNetServerPeer;
import com.whirvis.jraknet.protocol.ConnectionType;
import com.whirvis.jraknet.protocol.Reliability;
import com.whirvis.jraknet.protocol.message.CustomPacket;
import ink.flybird.cubecraft.client.ClientSharedContext;
import ink.flybird.cubecraft.net.NetHandlerContext;
import ink.flybird.cubecraft.net.packet.ConnectPacket;
import ink.flybird.cubecraft.net.packet.ConnectSuccessPacket;
import ink.flybird.cubecraft.net.packet.DisconnectPacket;
import ink.flybird.cubecraft.net.packet.Packet;
import ink.flybird.cubecraft.SharedContext;
import ink.flybird.fcommon.logging.Logger;
import ink.flybird.fcommon.logging.SimpleLogger;
import ink.flybird.fcommon.registry.ConstructingMap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

import java.net.InetSocketAddress;

public class RakNetClientIO extends ClientIO {
    private boolean running=false;
    private final Logger logger=new SimpleLogger("RakNetClientIO");
    private final RakNetClientListenerAdapter listener;
    private RakNetClient client;

    public RakNetClientIO() {
        this.listener = new RakNetClientListenerAdapter(SharedContext.PACKET, ClientSharedContext.NET_HANDLER);
    }

    @Override
    public ClientListenerAdapter getListener() {
        return this.listener;
    }

    @Override
    public void closeConnection() {
        this.running=false;
        this.listener.disconnect();
    }

    @Override
    public void connect(InetSocketAddress addr) {
        this.running=true;
        this.client = new RakNetClient();
        this.client.addListener(this.listener);
        try {
            this.client.connect(addr);
        } catch (RakNetException e) {
            this.logger.exception(e);
        }
    }

    public RakNetClient getClient() {
        return client;
    }

    @Override
    public boolean isRunning() {
        return running;
    }


    static public class RakNetClientListenerAdapter extends ClientListenerAdapter implements RakNetClientListener {
        private RakNetServerPeer server;
        private RakNetClient client;

        /**
         * an adapter of handler.
         *
         * @param packetConstructor packet registry.
         * @param handlerRegistry registry of handler.
         */
        public RakNetClientListenerAdapter(ConstructingMap<Packet> packetConstructor, ConstructingMap<ClientNetHandler> handlerRegistry) {
            super(packetConstructor, handlerRegistry);
        }


        @Override
        public void onConnect(RakNetClient client, InetSocketAddress address, ConnectionType connectionType) {
            this.callEvent(new ConnectPacket(address), new NetHandlerContext(address, this));
        }

        @Override
        public void onDisconnect(RakNetClient client, InetSocketAddress address, RakNetServerPeer peer, String reason) {
            this.callEvent(new DisconnectPacket(address, reason), new NetHandlerContext(address, this));
        }

        @Override
        public void onLogin(RakNetClient client, RakNetServerPeer peer) {
            this.server = peer;
            this.client = client;
            this.callEvent(new ConnectSuccessPacket(peer.getAddress()), new NetHandlerContext(peer.getAddress(), this));
        }

        @Override
        public void handleMessage(RakNetClient client, RakNetServerPeer peer, RakNetPacket packet, int channel) {
            Packet pkt = this.decode(packet.buffer());
            this.callEvent(pkt, new NetHandlerContext(peer.getAddress(), this));
        }

        public void disconnect() {
            this.client.disconnect();
        }

        @Override
        public void sendPacket(Packet pkt) {
            ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer();
            this.encode(pkt, buffer);
            byte[] arr = new byte[buffer.writerIndex()];
            buffer.resetReaderIndex();
            buffer.readBytes(arr);
            this.client.sendMessage(Reliability.RELIABLE,new CustomPacket(new RakNetPacket(arr)));
            buffer.release();
        }

        @Override
        public void onHandlerException(RakNetClient client, InetSocketAddress address, Throwable throwable) {
            throw new RuntimeException(throwable);
        }

        @Override
        public void onPeerException(RakNetClient client, RakNetServerPeer peer, Throwable throwable) {
            throw new RuntimeException(throwable);
        }

        public RakNetClient getClient() {
            return client;
        }
    }
}
