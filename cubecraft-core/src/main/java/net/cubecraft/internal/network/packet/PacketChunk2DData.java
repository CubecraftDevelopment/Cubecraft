package net.cubecraft.internal.network.packet;

import net.cubecraft.util.ByteBufUtil;
import net.cubecraft.net.packet.DataFragmentPacket;
import net.cubecraft.net.packet.PacketConstructor;
import net.cubecraft.world.chunk.Chunk;
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

@TypeItem("cubecraft:packet_chunk_2d_data")
public class PacketChunk2DData implements DataFragmentPacket<Chunk> {
    private String world;
    private long x, z;

    public PacketChunk2DData(String world, Chunk c) {
        this.x = c.getKey().getX();
        this.z = c.getKey().getZ();
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
        NBT.write(tag, new DataOutputStream(g));
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

        NBTTagCompound tag = (NBTTagCompound) NBT.read(new DataInputStream(in));
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
