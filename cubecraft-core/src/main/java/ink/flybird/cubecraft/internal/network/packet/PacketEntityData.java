package ink.flybird.cubecraft.internal.network.packet;

import ink.flybird.cubecraft.ContentRegistries;
import ink.flybird.cubecraft.net.ByteBufUtil;
import ink.flybird.cubecraft.net.packet.Packet;
import ink.flybird.cubecraft.world.entity.Entity;
import ink.flybird.fcommon.file.NBTBuilder;
import ink.flybird.fcommon.nbt.NBTTagCompound;
import io.netty.buffer.ByteBuf;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class PacketEntityData implements Packet {
    private Entity entity;

    public PacketEntityData(Entity entity) {
        this.entity = entity;
    }

    @Override
    public void writePacketData(ByteBuf buffer) throws Exception{
        ByteBufUtil.writeString(entity.getID(),buffer);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        GZIPOutputStream g = new GZIPOutputStream(out);
        NBTBuilder.write(this.entity.getData(), new DataOutputStream(g));
        g.close();
        ByteBufUtil.writeArray(out.toByteArray(), buffer);
    }

    @Override
    public void readPacketData(ByteBuf buffer) throws Exception{
        this.entity= ContentRegistries.ENTITY.create(ByteBufUtil.readString(buffer), (Object) null);
        GZIPInputStream in = new GZIPInputStream(new ByteArrayInputStream(ByteBufUtil.readArray(buffer)));
        NBTTagCompound tag = (NBTTagCompound) NBTBuilder.read(new DataInputStream(in));
        this.entity.setData(tag);
    }

    public Entity getEntity() {
        return entity;
    }
}
