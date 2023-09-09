package io.flybird.cubecraft.internal.network.packet.join;

import io.flybird.cubecraft.internal.entity.EntityPlayer;
import io.flybird.cubecraft.net.packet.Packet;
import io.flybird.cubecraft.net.packet.PacketConstructor;
import io.flybird.cubecraft.level.LevelInfo;
import ink.flybird.fcommon.registry.TypeItem;
import ink.flybird.fcommon.file.NBTBuilder;
import ink.flybird.fcommon.nbt.NBTTagCompound;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;

import java.io.IOException;

@TypeItem("cubecraft:join_world_response")
public class PacketPlayerJoinWorldResponse implements Packet {
    private LevelInfo levelInfo=new LevelInfo();
    private NBTTagCompound tag;

    public PacketPlayerJoinWorldResponse(LevelInfo info, EntityPlayer player) {
        levelInfo=info;
        this.tag=player.getData();
    }

    @PacketConstructor
    public PacketPlayerJoinWorldResponse(){
    }

    @Override
    public void writePacketData(ByteBuf buffer) {
        NBTTagCompound tag=new NBTTagCompound();
        tag.setCompoundTag("level",this.levelInfo.getData());
        tag.setCompoundTag("entity",this.tag);
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
        this.levelInfo.setData(tag.getCompoundTag("level"));
        this.tag=tag.getCompoundTag("entity");
    }

    public LevelInfo getLevelInfo() {
        return levelInfo;
    }

    public void setPlayerData(EntityPlayer player) {
        player.setData(this.tag);
    }
}