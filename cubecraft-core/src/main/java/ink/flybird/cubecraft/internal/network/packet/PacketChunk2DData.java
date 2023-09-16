package ink.flybird.cubecraft.internal.network.packet;

import ink.flybird.cubecraft.net.ByteBufUtil;
import ink.flybird.cubecraft.net.packet.DataFragmentPacket;
import ink.flybird.cubecraft.net.packet.PacketConstructor;
import ink.flybird.cubecraft.world.chunk.Chunk;
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

//todo:send and receive chunk data->Blocked chunk client provider
@TypeItem("cubecraft:packet_chunk_2d_data")
public class PacketChunk2DData implements DataFragmentPacket<Chunk> {
    private String world;
    private long x, z;

    public PacketChunk2DData(String world, Chunk c) {
        this.x = c.getKey().x();
        this.z = c.getKey().z();
        this.world = world;
        this.fetchData(c);
    }

    @PacketConstructor
    public PacketChunk2DData() {
    }

    @Override
    public void fetchData(Chunk chunk) {

    }

    @Override
    public void injectData(Chunk chunk) {

    }

    @Override
    public void writePacketData(ByteBuf buffer) throws Exception {
        buffer.writeLong(this.x);
        buffer.writeLong(this.z);

        NBTTagCompound tag = new NBTTagCompound();
        tag.setString("world", this.world);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        GZIPOutputStream g = new GZIPOutputStream(out);
        NBTBuilder.write(tag, new DataOutputStream(g));
        g.close();
        ByteBufUtil.writeArray(out.toByteArray(), buffer);
    }

    @Override
    public void readPacketData(ByteBuf buffer) throws Exception {
        this.x = buffer.readLong();
        this.z = buffer.readLong();

        byte[] arr = new byte[buffer.readInt()];
        buffer.readBytes(arr);
        GZIPInputStream in = new GZIPInputStream(new ByteArrayInputStream(arr));

        NBTTagCompound tag = (NBTTagCompound) NBTBuilder.read(new DataInputStream(in));
        this.world = tag.getString("world");
    }


    public long getX() {
        return x;
    }

    public long getZ() {
        return z;
    }

    public String getWorld() {
        return world;
    }
}
