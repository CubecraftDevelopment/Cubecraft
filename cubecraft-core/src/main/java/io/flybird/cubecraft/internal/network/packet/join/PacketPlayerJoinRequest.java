package io.flybird.cubecraft.internal.network.packet.join;

import io.flybird.cubecraft.auth.Session;
import io.flybird.cubecraft.net.packet.Packet;
import io.flybird.cubecraft.net.packet.PacketConstructor;
import io.flybird.cubecraft.register.SharedContext;
import ink.flybird.fcommon.registry.TypeItem;
import ink.flybird.fcommon.file.NBTBuilder;
import ink.flybird.fcommon.nbt.NBTTagCompound;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;

import java.io.IOException;

@TypeItem("cubecraft:join_request")
public class PacketPlayerJoinRequest implements Packet {
    private Session session=new Session("__DEFAULT__","cubecraft:default");

    public PacketPlayerJoinRequest(Session session) {
        this.session = session;
    }

    @PacketConstructor
    public PacketPlayerJoinRequest() {
    }

    @Override
    public void writePacketData(ByteBuf buffer) {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setString("type", this.session.getType());
        tag.setCompoundTag("data", SharedContext.SESSION_SERVICE.get(this.session.getType()).write(session));
        try {
            NBTBuilder.write(tag,new ByteBufOutputStream(buffer));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void readPacketData(ByteBuf buffer) {
        NBTTagCompound tag;
        try {
            tag = (NBTTagCompound) NBTBuilder.read(new ByteBufInputStream(buffer));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        SharedContext.SESSION_SERVICE.get(tag.getString("type")).read(session, tag.getCompoundTag("data"));
    }

    public Session getSession() {
        return session;
    }
}
