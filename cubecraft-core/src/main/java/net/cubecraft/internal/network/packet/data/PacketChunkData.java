package net.cubecraft.internal.network.packet.data;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import me.gb2022.commons.nbt.NBT;
import me.gb2022.commons.nbt.NBTTagCompound;
import net.cubecraft.net.packet.Packet;
import net.cubecraft.net.packet.PacketConstructor;
import net.cubecraft.util.ByteBufUtil;
import net.cubecraft.world.chunk.ChunkCodec;
import net.cubecraft.world.chunk.WorldChunk;

import java.io.IOException;

public final class PacketChunkData implements Packet {
    private final WorldChunk chunk;
    private final String world;

    public PacketChunkData(WorldChunk chunk) {
        this.chunk = chunk;
        this.world = chunk.getWorld().getId();
    }

    @PacketConstructor
    public PacketChunkData(ByteBuf buffer) throws IOException {
        var cx = buffer.readInt();
        var cz = buffer.readInt();

        this.chunk = new WorldChunk(null, cx, cz);
        this.world = ByteBufUtil.readString(buffer);

        try (var in = new ByteBufInputStream(buffer)) {
            ChunkCodec.setWorldChunkData(this.chunk, (NBTTagCompound) NBT.readZipped(in));
        }
    }

    @Override
    public void writePacketData(ByteBuf buffer) throws Exception {
        buffer.writeLong(this.chunk.getKey().getX());
        buffer.writeLong(this.chunk.getKey().getZ());
        ByteBufUtil.writeString(this.world, buffer);

        try (var out = new ByteBufOutputStream(buffer)) {
            NBT.writeZipped(ChunkCodec.getChunkData(this.chunk), out);
        }
    }

    public String getWorld() {
        return world;
    }

    public WorldChunk getChunk() {
        return chunk;
    }
}
