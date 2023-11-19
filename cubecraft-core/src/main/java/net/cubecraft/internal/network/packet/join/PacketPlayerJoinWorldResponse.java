package net.cubecraft.internal.network.packet.join;

import net.cubecraft.internal.entity.EntityPlayer;
import net.cubecraft.net.packet.Packet;
import net.cubecraft.net.packet.PacketConstructor;
import net.cubecraft.level.LevelInfo;
import ink.flybird.fcommon.registry.TypeItem;
import ink.flybird.fcommon.nbt.NBT;
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
        tag.setCompoundTag("level",this.levelInfo.getTag());
        tag.setCompoundTag("entity",this.tag);
        try {
            NBT.write(tag,new ByteBufOutputStream(buffer));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void readPacketData(ByteBuf buffer) {
        NBTTagCompound tag;
        try {
            tag = (NBTTagCompound) NBT.read(new ByteBufInputStream(buffer));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.levelInfo=new LevelInfo(tag.getCompoundTag("level"));
        this.tag=tag.getCompoundTag("entity");
    }

    public LevelInfo getLevelInfo() {
        return levelInfo;
    }

    public void setPlayerData(EntityPlayer player) {
        player.setData(this.tag);
    }
}