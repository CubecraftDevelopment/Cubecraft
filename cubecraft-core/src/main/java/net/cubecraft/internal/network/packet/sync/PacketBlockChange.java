package net.cubecraft.internal.network.packet.sync;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import me.gb2022.commons.nbt.NBT;
import me.gb2022.commons.nbt.NBTTagCompound;
import me.gb2022.commons.registry.TypeItem;
import net.cubecraft.net.packet.Packet;
import net.cubecraft.net.packet.PacketConstructor;
import net.cubecraft.world.block.BlockState;

/**
 * this packet hold a block change event to server
 */
@TypeItem("cubecraft:block_change")
public final class PacketBlockChange implements Packet {
    private final long x;
    private final long y;
    private final long z;
    private final String world;
    private final BlockState state;

    public PacketBlockChange(long x, long y, long z, String world, BlockState state) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.world = world;
        this.state = state;
    }

    @PacketConstructor
    public PacketBlockChange(ByteBuf buffer) {
        this.x = buffer.readLong();
        this.y = buffer.readLong();
        this.z = buffer.readLong();

        NBTTagCompound tag;
        tag = (NBTTagCompound) NBT.read(new ByteBufInputStream(buffer));
        this.world = tag.getString("world");
        this.state = new BlockState("", (byte) 0, (byte) 0);
        this.state.setData(tag.getCompoundTag("state"));
    }

    @Override
    public void writePacketData(ByteBuf buffer) {
        buffer.writeLong(x).writeLong(y).writeLong(z);
        NBTTagCompound tag = new NBTTagCompound();
        tag.setString("world", this.world);
        tag.setCompoundTag("state", state.getData());
        NBT.write(tag, new ByteBufOutputStream(buffer));
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
