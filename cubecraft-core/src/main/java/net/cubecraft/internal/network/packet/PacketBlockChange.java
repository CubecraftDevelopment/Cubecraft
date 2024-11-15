package net.cubecraft.internal.network.packet;

import me.gb2022.commons.nbt.NBT;
import me.gb2022.commons.nbt.NBTTagCompound;
import me.gb2022.commons.registry.TypeItem;
import net.cubecraft.net.packet.Packet;
import net.cubecraft.net.packet.PacketConstructor;
import net.cubecraft.world.block.BlockState;
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
        NBT.write(tag,new ByteBufOutputStream(buffer));
    }

    @Override
    public void readPacketData(ByteBuf buffer) {
        x=buffer.readLong();
        y=buffer.readLong();
        z=buffer.readLong();

        NBTTagCompound tag;
        tag = (NBTTagCompound) NBT.read(new ByteBufInputStream(buffer));
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
