package net.cubecraft.internal.network.packet;

import net.cubecraft.util.ByteBufUtil;
import net.cubecraft.net.packet.Packet;
import net.cubecraft.net.packet.PacketConstructor;
import net.cubecraft.world.chunk.task.ChunkLoadLevel;
import net.cubecraft.world.chunk.task.ChunkLoadTicket;
import net.cubecraft.world.chunk.pos.ChunkPos;

import me.gb2022.commons.registry.TypeItem;
import io.netty.buffer.ByteBuf;

@TypeItem("cubecraft:chunk_load")
public class PacketChunkLoad implements Packet {
    private String world;
    private long chunkX;
    private long chunkZ;
    private ChunkLoadTicket ticket = new ChunkLoadTicket(ChunkLoadLevel.None_TICKING, 514);

    public PacketChunkLoad(String world, ChunkPos pos, ChunkLoadTicket ticket) {
        this.world = world;
        this.chunkX = pos.getX();
        this.chunkZ = pos.getZ();
        this.ticket = ticket;
    }

    @PacketConstructor
    public PacketChunkLoad() {
    }

    @Override
    public void writePacketData(ByteBuf buffer) {
        buffer.writeLong(this.chunkX)
                .writeLong(this.chunkZ)
                .writeInt(this.ticket.getTime())
                .writeByte(this.ticket.getChunkLoadLevel().getOrder());
        ByteBufUtil.writeString(this.world,buffer);
    }

    @Override
    public void readPacketData(ByteBuf buffer) {
        this.chunkX = buffer.readLong();
        this.chunkZ = buffer.readLong();
        this.ticket.setTime(buffer.readInt());
        this.ticket.setChunkLoadLevel(ChunkLoadLevel.fromOrder(buffer.readByte()));
        this.world= ByteBufUtil.readString(buffer);
    }

    public long getChunkX() {
        return chunkX;
    }

    public long getChunkZ() {
        return chunkZ;
    }

    public ChunkLoadTicket getTicket() {
        return ticket;
    }

    public String getWorld() {
        return world;
    }
}
