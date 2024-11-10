package net.cubecraft.world.block.access;

import me.gb2022.commons.container.Vector3;
import me.gb2022.commons.math.hitting.HitBox;
import net.cubecraft.world.World;
import net.cubecraft.world.block.Block;
import net.cubecraft.world.block.EnumFacing;
import net.cubecraft.world.block.blocks.Blocks;
import net.cubecraft.world.block.property.BlockPropertyDispatcher;

import java.util.Collection;

public abstract class IBlockAccess implements BlockAccess {
    protected final World world;
    protected final long x;
    protected final long y;
    protected final long z;
    protected Block block;

    public IBlockAccess(World world, long x, long y, long z) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public abstract String getBlockID();

    public abstract EnumFacing getBlockFacing();


    public void setBlockID(String id, boolean sendUpdateEvent) {
    }

    public void setBlockFacing(EnumFacing facing, boolean sendUpdateEvent) {
    }

    @Override
    public void setBlockMeta(byte meta, boolean sendUpdateEvent) {
    }

    @Override
    public void setBlockLight(byte light, boolean sendUpdateEvent) {
    }

    @Override
    public void setBiome(String biome, boolean sendUpdateEvent) {
    }


    @Override
    public Block getBlock() {
        return Blocks.REGISTRY.registered(getBlockId()).get();
    }



    @Override
    public void scheduleTick(int time) {
        this.world.setTickSchedule(this.x, this.y, this.z, time);
    }

    @Override
    public Collection<HitBox> getHitBox() {
        return BlockPropertyDispatcher.getHitBox(this);
    }

    @Override
    public long getX() {
        return this.x;
    }

    @Override
    public long getY() {
        return this.y;
    }

    @Override
    public long getZ() {
        return this.z;
    }


    @Override
    public IBlockAccess getNear(EnumFacing facing) {
        Vector3<Long> pos = facing.findNear(this.x, this.y, this.z, 1);
        return this.world.getBlockAccess(pos.x(), pos.y(), pos.z());
    }

    @Override
    public World getWorld() {
        return world;
    }

    @Override
    public void setBlockId(int id, boolean silent) {
    }
}
