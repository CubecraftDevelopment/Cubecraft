package net.cubecraft.internal.network.packet;

import net.cubecraft.util.ByteBufUtil;
import net.cubecraft.net.packet.Packet;
import net.cubecraft.net.packet.PacketConstructor;
import net.cubecraft.world.chunk.pos.ChunkPos;
import me.gb2022.commons.registry.TypeItem;
import io.netty.buffer.ByteBuf;

@TypeItem("cubecraft:chunk_get")
public class PacketChunkGet implements Packet {
    private long chunkX;
    private long chunkZ;
    private String world="cubecraft:overworld";

    public PacketChunkGet(ChunkPos pos, String world){
        this.chunkX=pos.getX();
        this.chunkZ=pos.getZ();
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
