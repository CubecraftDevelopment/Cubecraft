package ink.flybird.cubecraft.internal.network.packet;

import ink.flybird.cubecraft.net.ByteBufUtil;
import ink.flybird.cubecraft.net.packet.Packet;
import ink.flybird.cubecraft.net.packet.PacketConstructor;
import ink.flybird.cubecraft.world.chunk.ChunkPos;
import ink.flybird.cubecraft.world.chunk.WorldChunk;
import ink.flybird.fcommon.file.NBTBuilder;
import ink.flybird.fcommon.nbt.NBTTagCompound;
import ink.flybird.fcommon.registry.TypeItem;
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
        this.world = chunk.getWorld().getID();
    }

    @PacketConstructor
    public PacketFullChunkData() {
    }


    @Override
    public void writePacketData(ByteBuf buffer) throws Exception {
        buffer.writeLong(this.chunk.getKey().x());
        buffer.writeLong(this.chunk.getKey().z());
        ByteBufUtil.writeString(this.world, buffer);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        GZIPOutputStream g = new GZIPOutputStream(out);
        NBTBuilder.write(chunk.getData(), new DataOutputStream(g));
        g.close();
        ByteBufUtil.writeArray(out.toByteArray(), buffer);
    }

    @Override
    public void readPacketData(ByteBuf buffer) throws Exception {
        this.chunk = new WorldChunk(null, new ChunkPos(buffer.readLong(), buffer.readLong()));
        this.world = ByteBufUtil.readString(buffer);
        byte[] arr = new byte[buffer.readInt()];
        buffer.readBytes(arr);
        GZIPInputStream in = new GZIPInputStream(new ByteArrayInputStream(arr));

        NBTTagCompound tag = (NBTTagCompound) NBTBuilder.read(new DataInputStream(in));
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
