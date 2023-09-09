package io.flybird.cubecraft.internal.network.packet;

import io.flybird.cubecraft.net.ByteBufUtil;
import io.flybird.cubecraft.net.packet.Packet;
import io.flybird.cubecraft.net.packet.PacketConstructor;
import io.flybird.cubecraft.world.chunk.ChunkPos;
import ink.flybird.fcommon.registry.TypeItem;
import io.netty.buffer.ByteBuf;

@TypeItem("cubecraft:chunk_get")
public class PacketChunkGet implements Packet {
    private long chunkX;
    private long chunkZ;
    private String world="cubecraft:overworld";

    public PacketChunkGet(ChunkPos pos, String world){
        this.chunkX=pos.x();
        this.chunkZ=pos.z();
        this.world=world;
    }

    @PacketConstructor
    public PacketChunkGet(){
    }


    @Override
    public void writePacketData(ByteBuf buffer) {
        buffer.writeLong(getChunkX()).
                writeLong(getChunkZ());
        ByteBufUtil.writeString(this.world,buffer);
    }

    @Override
    public void readPacketData(ByteBuf buffer) {
        this.chunkX =buffer.readLong();
        this.chunkZ =buffer.readLong();
        this.world= ByteBufUtil.readString(buffer);
    }

    public long getChunkX() {
        return this.chunkX;
    }

    public long getChunkZ() {
        return this.chunkZ;
    }

    public String getWorld() {
        return world;
    }
}
