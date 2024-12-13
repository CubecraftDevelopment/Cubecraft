package net.cubecraft.internal.network.packet.data;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import me.gb2022.commons.nbt.NBT;
import me.gb2022.commons.nbt.NBTTagCompound;
import net.cubecraft.internal.entity.EntityPlayer;
import net.cubecraft.net.packet.Packet;
import net.cubecraft.net.packet.PacketConstructor;
import net.cubecraft.util.ByteBufUtil;
import net.cubecraft.world.entity.Entity;

import java.io.IOException;

public final class PacketEntityData implements Packet {
    private final Entity entity;
    private final String type;

    public PacketEntityData(Entity entity) {
        this.entity = entity;
        this.type = entity.getType();
    }

    @PacketConstructor
    public PacketEntityData(ByteBuf buffer) throws IOException {
        this.type = ByteBufUtil.readString(buffer);
        this.entity = new EntityPlayer(null, null);//todo:create entity

        try (var in = new ByteBufInputStream(buffer)) {
            this.entity.setData((NBTTagCompound) NBT.readZipped(in));
        }
    }

    @Override
    public void writePacketData(ByteBuf buffer) throws Exception {
        ByteBufUtil.writeString(this.type, buffer);
        try (var out = new ByteBufOutputStream(buffer)) {
            NBT.writeZipped(this.entity.getData(), out);
        }
    }
}
