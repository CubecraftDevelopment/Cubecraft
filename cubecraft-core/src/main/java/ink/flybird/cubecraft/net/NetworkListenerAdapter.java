package ink.flybird.cubecraft.net;

import ink.flybird.fcommon.event.EventBus;
import ink.flybird.fcommon.logging.Logger;
import ink.flybird.fcommon.logging.SimpleLogger;
import ink.flybird.fcommon.registry.ConstructingMap;
import ink.flybird.fcommon.registry.TypeItem;
import ink.flybird.cubecraft.net.packet.Packet;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * an abstracted general adapter of handler
 *
 * @author GrassBlock2022
 */
public abstract class NetworkListenerAdapter extends NetWorkEventBus {
    public static final ExecutorService HANDLER_POOL = Executors.newFixedThreadPool(8);
    protected final Logger logger=new SimpleLogger("NetworkListenerAdapter");
    protected final ConstructingMap<Packet> packetConstructor;

    /**
     * an adapter of handler.
     *
     * @param packetConstructor packet registry.
     */
    public NetworkListenerAdapter(ConstructingMap<Packet> packetConstructor) {
        this.packetConstructor = packetConstructor;
    }

    /**
     * encode a packet data(include type etc.) to a buffer.
     *
     * @param pkt    packet
     * @param buffer target buffer
     */
    public final void encode(Packet pkt, ByteBuf buffer) {
        buffer.resetReaderIndex();
        buffer.resetWriterIndex();

        //confuse jraknet
        buffer.writeByte(0xFF);

        //encode real data
        TypeItem type = pkt.getClass().getAnnotation(TypeItem.class);
        if (type == null) {
            logger.error("no type annotated in current class of packet:" + pkt.getClass().getName());
            return;
        }
        byte[] head = type.value().getBytes(StandardCharsets.US_ASCII);
        buffer.writeByte(head.length);
        buffer.writeBytes(head);
        try {
            ByteBuf d = ByteBufAllocator.DEFAULT.ioBuffer();
            pkt.writePacketData(d);
            byte[] data = new byte[d.writerIndex()];
            d.readBytes(data);
            d.release();

            buffer.writeInt(data.length);
            buffer.writeBytes(data);
        } catch (Exception e) {
            this.logger.error("find exception when writing packet:" + e.getMessage());
            this.logger.exception(e);
        }
    }

    /**
     * decode a packet with type from raw buffer.
     *
     * @param buffer buffer
     * @return decoded packet
     */
    public final Packet decode(ByteBuf buffer) {
        buffer.resetReaderIndex();

        //but don`t confuse me
        buffer.readByte();

        //get type
        byte type_l = buffer.readByte();
        byte[] type_raw = new byte[type_l];
        buffer.readBytes(type_raw);
        String type = new String(type_raw, StandardCharsets.US_ASCII);

        //data
        int data_l = buffer.readInt();
        ByteBuf data = buffer.readBytes(data_l);

        //packet
        Packet pkt = this.packetConstructor.create(type);
        try {
            pkt.readPacketData(data);
        } catch (Exception e) {
            this.logger.error("find exception when decoding packet:" + e.getMessage());
            this.logger.exception(e);
        }
        data.release();

        return pkt;
    }

    /**
     * kick a connection.
     *
     * @param address addr that you want to kick.
     */
    public abstract void disconnect(InetSocketAddress address);

    /**
     * send a packet to an object
     *
     * @param pkt     packet
     * @param address target
     */
    public abstract void sendPacket(Packet pkt, InetSocketAddress address);

    public void attachEventBus(EventBus bus) {
        for (Object listener : this.getHandlers()) {
            bus.registerEventListener(listener);
        }
    }

    public void removeEventBus(EventBus bus) {
        for (Object listener : this.getHandlers()) {
            bus.unregisterEventListener(listener);
        }
    }

    public void runTask(Runnable command) {
        HANDLER_POOL.execute(command);
    }
}
