package net.cubecraft.internal.network.packet.join;

import net.cubecraft.internal.entity.EntityPlayer;
import net.cubecraft.net.packet.Packet;
import net.cubecraft.net.packet.PacketConstructor;
import net.cubecraft.level.LevelInfo;
import me.gb2022.commons.registry.TypeItem;
import me.gb2022.commons.nbt.NBT;
import me.gb2022.commons.nbt.NBTTagCompound;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;

@TypeItem("cubecraft:join_world_response")
public class PacketPlayerJoinWorldResponse implements Packet {
    private LevelInfo levelInfo=new LevelInfo(null,null);
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
        tag.setCompoundTag("level", this.levelInfo.getData());
        tag.setCompoundTag("entity",this.tag);
        NBT.write(tag,new ByteBufOutputStream(buffer));
    }

    @Override
    public void readPacketData(ByteBuf buffer) {
        NBTTagCompound tag;
        tag = (NBTTagCompound) NBT.read(new ByteBufInputStream(buffer));
        this.levelInfo=new LevelInfo(null,tag.getCompoundTag("level"));
        this.tag=tag.getCompoundTag("entity");
    }

    public LevelInfo getLevelInfo() {
        return levelInfo;
    }

    public void setPlayerData(EntityPlayer player) {
        player.setData(this.tag);
    }
}