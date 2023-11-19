package net.cubecraft.world.block;

import ink.flybird.fcommon.math.hitting.HitBox;
import ink.flybird.fcommon.math.hitting.Hittable;
import ink.flybird.fcommon.nbt.NBTDataIO;
import ink.flybird.fcommon.nbt.NBTTagCompound;
import net.cubecraft.ContentRegistries;

import java.util.Collection;

@Deprecated
public class BlockState implements NBTDataIO, Hittable {
    private long x;
    private long y;
    private long z;

    private byte facing;
    private String id;
    private byte meta;

    public BlockState(String id, byte facing, byte meta) {
        this.id = id;
        this.meta = meta;
        this.facing = facing;
    }

    //data
    public String getId() {
        return id;
    }

    public BlockState setId(String id) {
        this.id = id;
        return this;
    }

    public EnumFacing getFacing() {
        return EnumFacing.fromId(this.facing);
    }

    public BlockState setFacing(EnumFacing f) {
        this.facing = f.numID;
        return this;
    }

    public BlockState setFacing(byte f) {
        this.facing = f;
        return this;
    }

    public Block getBlock() {
        return ContentRegistries.BLOCK.get(this.id);
    }

    public byte getMeta() {
        return meta;
    }


    //io
    @Override
    public NBTTagCompound getData() {
        NBTTagCompound compound = new NBTTagCompound();
        compound.setByte("facing", this.facing);
        compound.setString("id", this.id);
        compound.setByte("meta", meta);
        return compound;
    }

    @Override
    public void setData(NBTTagCompound compound) {
        this.facing = compound.getByte("facing");
        this.id = compound.getString("id");
        this.meta = compound.getByte("meta");
    }

    @Override
    public Collection<HitBox> getHitBox() {
        return null;
    }

    public long getX() {
        return x;
    }

    public BlockState setX(long x) {
        this.x = x;
        return this;
    }

    public long getY() {
        return y;
    }

    public BlockState setY(long y) {
        this.y = y;
        return this;
    }

    public long getZ() {
        return z;
    }

    public BlockState setZ(long z) {
        this.z = z;
        return this;
    }
}