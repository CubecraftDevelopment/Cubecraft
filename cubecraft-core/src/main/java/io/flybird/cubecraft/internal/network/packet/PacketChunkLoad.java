package io.flybird.cubecraft.internal.network.packet;

import io.flybird.cubecraft.net.ByteBufUtil;
import io.flybird.cubecraft.net.packet.Packet;
import io.flybird.cubecraft.net.packet.PacketConstructor;
import io.flybird.cubecraft.world.chunk.ChunkLoadLevel;
import io.flybird.cubecraft.world.chunk.ChunkLoadTicket;
import io.flybird.cubecraft.world.chunk.ChunkPos;

import ink.flybird.fcommon.registry.TypeItem;
import io.netty.buffer.ByteBuf;

@TypeItem("cubecraft:chunk_load")
public class PacketChunkLoad implements Packet {
    private String world;
    private long chunkX;
    private long chunkZ;
    private ChunkLoadTicket ticket = new ChunkLoadTicket(ChunkLoadLevel.None_TICKING, 514);

    public PacketChunkLoad(String world, ChunkPos pos, ChunkLoadTicket ticket) {
        this.world = world;
        this.chunkX = pos.x();
        this.chunkZ = pos.z();
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
