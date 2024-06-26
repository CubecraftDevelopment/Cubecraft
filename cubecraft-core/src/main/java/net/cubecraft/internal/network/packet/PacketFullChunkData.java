package net.cubecraft.internal.network.packet;

import net.cubecraft.util.ByteBufUtil;
import net.cubecraft.net.packet.Packet;
import net.cubecraft.net.packet.PacketConstructor;
import net.cubecraft.world.chunk.pos.ChunkPos;
import net.cubecraft.world.chunk.WorldChunk;
import me.gb2022.commons.nbt.NBT;
import me.gb2022.commons.nbt.NBTTagCompound;
import me.gb2022.commons.registry.TypeItem;
import io.netty.buffer.ByteBuf;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

@TypeItem("cubecraft:full_chunk_data")
public class PacketFullChunkData implements Packet {
    private WorldChunk chunk;
    private String world;

    public PacketFullChunkData(WorldChunk chunk) {
        this.chunk = chunk;
        this.world = chunk.getWorld().getId();
    }

    @PacketConstructor
    public PacketFullChunkData() {
    }


    @Override
    public void writePacketData(ByteBuf buffer) throws Exception {
        buffer.writeLong(this.chunk.getKey().getX());
        buffer.writeLong(this.chunk.getKey().getZ());
        ByteBufUtil.writeString(this.world, buffer);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        GZIPOutputStream g = new GZIPOutputStream(out);
        NBT.write(chunk.getData(), new DataOutputStream(g));
        g.close();
        ByteBufUtil.writeArray(out.toByteArray(), buffer);
    }

    @Override
    public void readPacketData(ByteBuf buffer) throws Exception {
        this.chunk = new WorldChunk(null, ChunkPos.create(buffer.readLong(), buffer.readLong()));
        this.world = ByteBufUtil.readString(buffer);
        byte[] arr = new byte[buffer.readInt()];
        buffer.readBytes(arr);
        GZIPInputStream in = new GZIPInputStream(new ByteArrayInputStream(arr));

        NBTTagCompound tag = (NBTTagCompound) NBT.read(new DataInputStream(in));
        in.close();
        this.chunk.setData(tag);
    }

    public String getWorld() {
        return world;
    }

    public WorldChunk getChunk() {
        return chunk;
    }
}
