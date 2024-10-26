package net.cubecraft.world.block.access;

import me.gb2022.commons.container.Vector3;
import me.gb2022.commons.math.AABB;
import me.gb2022.commons.math.hitting.HitBox;
import me.gb2022.commons.math.hitting.Hittable;
import net.cubecraft.CoreRegistries;
import net.cubecraft.util.register.Registered;
import net.cubecraft.world.World;
import net.cubecraft.world.biome.Biome;
import net.cubecraft.world.block.Block;
import net.cubecraft.world.block.EnumFacing;
import net.cubecraft.world.block.property.BlockPropertyDispatcher;
import net.cubecraft.world.block.property.collision.CollisionProperty;

import java.util.Collection;
import java.util.List;

public abstract class IBlockAccess implements Hittable {
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

    public abstract int getBlockId();

    public abstract String getBlockID();

    public abstract EnumFacing getBlockFacing();

    public abstract byte getBlockMeta();

    public abstract byte getBlockLight();

    public abstract Biome getBiome();


    public void setBlockID(String id, boolean sendUpdateEvent) {
    }

    public void setBlockFacing(EnumFacing facing, boolean sendUpdateEvent) {
    }

    public void setBlockMeta(byte meta, boolean sendUpdateEvent) {
    }

    public void setBlockLight(byte light, boolean sendUpdateEvent) {
    }

    public void setBiome(String biome, boolean sendUpdateEvent) {
    }


    public Block getBlock() {
        return CoreRegistries.BLOCKS.registered(getBlockId()).get();
    }



    public void scheduleTick(int time) {
        this.world.setTickSchedule(this.x, this.y, this.z, time);
    }

    public Collection<AABB> getCollisionBox() {
        var block = getBlock();
        if (block == null) {
            return List.of();
        }

        CollisionProperty property = block.getBlockProperty("cubecraft:collision", CollisionProperty.class);
        if (property == null) {
            return List.of();
        }
        return property.get(this);
    }

    @Override
    public Collection<HitBox> getHitBox() {
        return BlockPropertyDispatcher.getHitBox(this);
    }

    public long getX() {
        return this.x;
    }

    public long getY() {
        return this.y;
    }

    public long getZ() {
        return this.z;
    }


    public IBlockAccess getNear(EnumFacing facing) {
        Vector3<Long> pos = facing.findNear(this.x, this.y, this.z, 1);
        return this.world.getBlockAccess(pos.x(), pos.y(), pos.z());
    }

    public World getWorld() {
        return world;
    }

    public abstract void setBiome(Registered<Biome> biome, boolean sendUpdateEvent);
}
