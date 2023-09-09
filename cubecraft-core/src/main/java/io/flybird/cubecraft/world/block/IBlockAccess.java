package io.flybird.cubecraft.world.block;

import ink.flybird.fcommon.math.AABB;
import ink.flybird.fcommon.math.HitBox;
import ink.flybird.fcommon.math.HitResult;
import ink.flybird.fcommon.math.HittableObject;
import io.flybird.cubecraft.register.ContentRegistries;
import io.flybird.cubecraft.world.IDimension;
import io.flybird.cubecraft.world.IWorld;
import io.flybird.cubecraft.world.entity.Entity;

public abstract class IBlockAccess implements HittableObject<Entity,IWorld> {
    protected final IWorld world;
    protected final long x;
    protected final long y;
    protected final long z;
    protected final IDimension dimension;

    public IBlockAccess(IWorld world, long x, long y, long z) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.dimension = this.world.getDimension();
    }

    public abstract String getBlockID();

    public void setBlockID(String id, boolean sendUpdateEvent) {
    }

    public abstract EnumFacing getBlockFacing();

    public void setBlockFacing(EnumFacing facing, boolean sendUpdateEvent) {
    }

    public abstract byte getBlockMeta();

    public void setBlockMeta(byte meta, boolean sendUpdateEvent) {
    }

    public abstract byte getBlockLight();

    public void setBlockLight(byte light, boolean sendUpdateEvent) {
    }

    public abstract String getBiome();

    public void setBiome(String biome, boolean sendUpdateEvent) {
    }

    public abstract byte getTemperature();

    public void setTemperature(byte temperature, boolean sendUpdateEvent) {
    }

    public abstract byte getHumidity();

    public void setHumidity(byte humidity, boolean sendUpdateEvent) {
    }

    public Block getBlock() {
        return ContentRegistries.BLOCK.get(this.getBlockID());
    }

    public void scheduleTick(int time){
        this.world.setTickSchedule(this.x,this.y,this.z,time);
    }

    public AABB[] getCollisionBox(){
        return new AABB[0];
    }

    public HitBox<Entity,IWorld>[] getSelectionBox(){
        return new HitBox[0];
    }

    @Override
    public void onHit(Entity from, IWorld world, HitResult<Entity, IWorld> hr) {}

    @Override
    public void onInteract(Entity from, IWorld world, HitResult<Entity, IWorld> hr) {}

    public long getX() {
        return this.x;
    }

    public long getY() {
        return this.y;
    }

    public long getZ() {
        return this.z;
    }
}
