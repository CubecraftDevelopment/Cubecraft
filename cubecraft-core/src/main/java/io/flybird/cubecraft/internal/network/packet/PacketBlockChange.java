package io.flybird.cubecraft.internal.network.packet;

import ink.flybird.fcommon.file.NBTBuilder;
import ink.flybird.fcommon.nbt.NBTTagCompound;
import ink.flybird.fcommon.registry.TypeItem;
import io.flybird.cubecraft.net.packet.Packet;
import io.flybird.cubecraft.net.packet.PacketConstructor;
import io.flybird.cubecraft.world.block.BlockState;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;

import java.io.IOException;

/**
 * this packet hold a block change event to server
 */
@TypeItem("cubecraft:block_change")
public class PacketBlockChange implements Packet {
    private long x;
    private long y;
    private long z;
    private String world;
    private BlockState state;

    public PacketBlockChange(long x, long y, long z, String world, BlockState state) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.world = world;
        this.state = state;
    }

    @PacketConstructor
    public PacketBlockChange(){}

    @Override
    public void writePacketData(ByteBuf buffer) {
        buffer.writeLong(x).writeLong(y).writeLong(z);
        NBTTagCompound tag=new NBTTagCompound();
        tag.setString("world",this.world);
        tag.setCompoundTag("state",state.getData());
        try {
            NBTBuilder.write(tag,new ByteBufOutputStream(buffer));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void readPacketData(ByteBuf buffer) {
        x=buffer.readLong();
        y=buffer.readLong();
        z=buffer.readLong();

        NBTTagCompound tag;
        try {
            tag = (NBTTagCompound) NBTBuilder.read(new ByteBufInputStream(buffer));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.world=tag.getString("world");
        this.state.setData(tag.getCompoundTag("state"));
    }

    public long getX() {
        return x;
    }

    public long getY() {
        return y;
    }

    public long getZ() {
        return z;
    }

    public BlockState getState() {
        return state;
    }

    public String getWorld() {
        return world;
    }
}
