package net.cubecraft.net.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import me.gb2022.commons.nbt.NBT;
import me.gb2022.commons.registry.TypeItem;
import net.cubecraft.net.packet.Packet;
import net.cubecraft.world.chunk.ChunkCodec;
import net.cubecraft.world.chunk.WorldChunk;

@TypeItem("cubecraft:chunk_data")
public final class PacketChunkData implements Packet {
    private WorldChunk chunk;

    @Override
    public void writePacketData(ByteBuf buffer) throws Exception {
        NBT.writeZipped(ChunkCodec.getChunkData(this.chunk), new ByteBufOutputStream(buffer));
    }

    @Override
    public void readPacketData(ByteBuf buffer) throws Exception {

    }
}
