package io.flybird.cubecraft.internal.network.packet.chunk;

import io.flybird.cubecraft.net.ByteBufUtil;
import io.flybird.cubecraft.net.packet.Packet;
import io.netty.buffer.ByteBuf;

public abstract class PacketChunkDataSection implements Packet {
    protected String world;
    protected long chunkX;
    protected long chunkZ;
    protected int sectionIndex;

    protected PacketChunkDataSection(String world, long chunkX, long chunkZ, int sectionIndex) {
        this.world = world;
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
        this.sectionIndex = sectionIndex;
    }

    @Override
    public void writePacketData(ByteBuf buffer) throws Exception {
        buffer.writeLong(this.chunkX);
        buffer.writeLong(this.chunkZ);
        buffer.writeInt(this.sectionIndex);
        ByteBufUtil.writeString(this.world, buffer);
    }

    @Override
    public void readPacketData(ByteBuf buffer) throws Exception {
        this.chunkX = buffer.readLong();
        this.chunkZ = buffer.readLong();
        this.sectionIndex = buffer.readInt();
        this.world = ByteBufUtil.readString(buffer);
    }


    public long getChunkX() {
        return this.chunkX;
    }

    public long getChunkZ() {
        return this.chunkZ;
    }

    public int getSectionIndex() {
        return this.sectionIndex;
    }

    public String getWorld() {
        return world;
    }
}
