import me.gb2022.commons.nbt.NBTTagCompound;
import me.gb2022.commons.registry.TypeItem;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import net.cubecraft.SharedContext;
import net.cubecraft.client.net.base.ClientContext;
import net.cubecraft.client.net.base.ClientListener;
import net.cubecraft.client.net.kcp.KCPNetworkClient;
import net.cubecraft.util.ByteBufUtil;
import net.cubecraft.net.packet.Packet;
import net.cubecraft.server.net.base.ServerContext;
import net.cubecraft.server.net.base.ServerListener;
import net.cubecraft.server.net.kcp.KCPNetworkServer;
import net.cubecraft.util.NBTBufferCodec;
import net.cubecraft.world.chunk.Chunk;
import net.cubecraft.world.chunk.ChunkCodec;
import net.cubecraft.world.chunk.ProviderChunk;
import net.cubecraft.world.chunk.pos.ChunkPos;

import java.net.InetSocketAddress;
import java.util.Random;

public class Test {
    public static final KCPNetworkServer server = new KCPNetworkServer();
    public static final KCPNetworkClient client1 = new KCPNetworkClient();

    //todo:packet handling

    public static void main2(String[] args) {
        SharedContext.PACKET.registerItem(CustomMessage.class);

        final int[] pps = {0};
        InetSocketAddress address = new InetSocketAddress("127.0.0.1", 63342);

        server.addHandler(CustomMessage.class, (packet, context) -> {
            context.sendPacket(context.getClientAddress(), new CustomMessage());
        });

        client1.addHandler(CustomMessage.class, (packet, context) -> {
            pps[0]++;
            context.sendPacket(new CustomMessage());
        });

        server.addListener(new ServerListener() {
            @Override
            public void handleException(Throwable exception, ServerContext context) {
                exception.printStackTrace();
            }
        });

        client1.addListener(new ClientListener() {
            @Override
            public void handleException(Throwable exception, ClientContext context) {
                exception.printStackTrace();
            }
        });

        server.start(address);
        client1.connect(address);
        client1.sendPacket(new CustomMessage());


        long last = System.currentTimeMillis();

        while (true) {
            if (System.currentTimeMillis() - last > 1000) {
                System.out.println(pps[0]);
                pps[0] = 0;
                last = System.currentTimeMillis();
            }
        }
    }

    public static void main(String[] args) {
        Chunk c=new ProviderChunk(new ChunkPos(0,0));
        NBTTagCompound tag=ChunkCodec.getChunkSection(c,0);

        ByteBuf byteBuf=ByteBufAllocator.DEFAULT.ioBuffer();

        NBTBufferCodec.writeCompressedNBT(byteBuf, tag);

        System.out.println(byteBuf.writerIndex());
    }

    @TypeItem("test:test")
    public static class CustomMessage implements Packet {
        public static final byte[] b = new byte[1024];

        static {
            new Random().nextBytes(b);
        }

        @Override
        public void writePacketData(ByteBuf buffer) {
            buffer.writeBytes(b);
        }

        @Override
        public void readPacketData(ByteBuf buffer) {

        }
    }
}
