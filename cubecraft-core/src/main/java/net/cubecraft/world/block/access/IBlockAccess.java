package net.cubecraft.world.block.access;

import me.gb2022.commons.container.Vector3;
import net.cubecraft.world.BlockAccessor;
import net.cubecraft.world.World;
import net.cubecraft.world.block.Block;
import net.cubecraft.world.block.EnumFacing;

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

    @Override
    public void scheduleTick(int time) {
        this.world.setTickSchedule(this.x, this.y, this.z, time);
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
    public BlockAccess getNear(EnumFacing facing) {
        Vector3<Long> pos = facing.findNear(this.x, this.y, this.z, 1);
        return this.world.getBlockAccess(pos.x(), pos.y(), pos.z());
    }

    @Override
    public BlockAccessor getWorld() {
        return world;
    }

    @Override
    public void setBlockId(int id, boolean silent) {
    }
}
