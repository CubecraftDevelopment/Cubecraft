package net.cubecraft.internal.network.packet.chunk;

import net.cubecraft.internal.network.packet.PacketType;
import net.cubecraft.net.ByteBufUtil;
import net.cubecraft.net.packet.Packet;
import net.cubecraft.net.packet.PacketConstructor;
import net.cubecraft.world.chunk.Chunk;
import net.cubecraft.world.chunk.ChunkCodec;
import ink.flybird.fcommon.nbt.NBT;
import ink.flybird.fcommon.nbt.NBTTagCompound;
import ink.flybird.fcommon.registry.TypeItem;
import io.netty.buffer.ByteBuf;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * takes 7 data channels of a chunk.
 *
 * @author GrassBlock2022
 */
@TypeItem(PacketType.CHUNK_SECTION)
public class PacketChunkSection implements Packet {
    private NBTTagCompound data;
    private String world;
    private long x, z;
    private int y;

    public PacketChunkSection(Chunk chunk, String world, long z, int y, long x) {
        this.world = world;
        this.x = x;
        this.z = z;
        this.data = ChunkCodec.getChunkSection(chunk, y);
        this.y = y;
    }

    @PacketConstructor
    public PacketChunkSection() {
    }

    @Override
    public void writePacketData(ByteBuf buffer) throws Exception {
        buffer.writeLong(this.x);
        buffer.writeLong(this.z);
        buffer.writeInt(this.y);
        ByteBufUtil.writeString(this.world, buffer);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        GZIPOutputStream g = new GZIPOutputStream(out);
        NBT.write(this.data, new DataOutputStream(g));
        g.close();
        ByteBufUtil.writeArray(out.toByteArray(), buffer);
    }

    @Override
    public void readPacketData(ByteBuf buffer) throws Exception {
        this.x = buffer.readLong();
        this.z = buffer.readLong();
        this.y = buffer.readInt();
        this.world = ByteBufUtil.readString(buffer);

        byte[] arr = new byte[buffer.readInt()];
        buffer.readBytes(arr);
        GZIPInputStream in = new GZIPInputStream(new ByteArrayInputStream(arr));
        this.data = (NBTTagCompound) NBT.read(new DataInputStream(in));
    }

    public int getY() {
        return y;
    }

    public long getX() {
        return x;
    }

    public NBTTagCompound getData() {
        return data;
    }

    public long getZ() {
        return z;
    }
}
