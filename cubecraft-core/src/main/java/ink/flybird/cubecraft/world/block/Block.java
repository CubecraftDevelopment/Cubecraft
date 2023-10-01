package ink.flybird.cubecraft.world.block;


import ink.flybird.cubecraft.event.BlockRegisterEvent;
import ink.flybird.cubecraft.ContentRegistries;
import ink.flybird.cubecraft.SharedContext;
import ink.flybird.cubecraft.world.IWorld;
import ink.flybird.cubecraft.world.block.access.IBlockAccess;
import ink.flybird.cubecraft.world.block.controller.BlockController;
import ink.flybird.cubecraft.world.entity.Entity;
import ink.flybird.cubecraft.world.entity.EntityItem;
import ink.flybird.fcommon.container.Vector3;
import ink.flybird.fcommon.math.AABB;
import ink.flybird.fcommon.math.HitBox;
import ink.flybird.fcommon.math.HittableObject;
import org.joml.Vector3d;

import java.util.Arrays;
import java.util.HashMap;

/**
 * defines all data for a block.
 * STRUCTURE FINISHED-2022-6-14
 */
public abstract class Block {
    private final HashMap<String, Object> properties = new HashMap<>(64);
    private final HashMap<String, BlockController> controllers = new HashMap<>(64);

    public Block(String defaultRegisterId) {
        SharedContext.MOD.getModLoaderEventBus().callEvent(new BlockRegisterEvent(defaultRegisterId, this));
    }


    public Block() {
    }

    public <T> T getBlockProperty(String id, Class<T> type) {
        return type.cast(this.properties.get(id));
    }
//  ------ physic ------

    /**
     * defines which facings are valid
     *
     * @return facings
     */
    public abstract EnumFacing[] getEnabledFacings();

    /**
     * defines collision boxes(relative)
     *
     * @return boxes
     */
    public abstract AABB[] getCollisionBoxSizes();

    /**
     * defines selection boxes(relative)
     *
     * @return boxes
     */
    public abstract AABB[] getSelectionBoxSizes();

    /**
     * defines resistance of a block. If entity goes into it,speed will multiply this value.
     *
     * @return resistance(0.0 ~ 1.0)
     */
    public abstract float getResistance();

    /**
     * defines whatever entity flows up or gets down when into it base on the value bigger or smaller from entity`s density
     *
     * @return density
     */
    public abstract float getDensity();

    public abstract float getHardNess();

    public abstract boolean isSolid();

    public abstract int opacity();

    public int getLight() {
        return 0;
    }

//  ------ ticking ------

    /**
     * when a block updates,this will be invoked.
     *
     * @param world happened dimension
     * @param x     happened position
     * @param y     happened position
     * @param z     happened position
     */
    public void onBlockUpdate(IWorld world, long x, long y, long z) {
    }

    //this part is about attributes :)
    public abstract String getID();

    public abstract String[] getTags();

    public EntityItem[] getDrop(IWorld world, long x, long y, long z, Entity from) {
        return new EntityItem[0];
    }

//  ------ general getter ------

    public AABB[] getCollisionBox(long x, long y, long z) {
        AABB[] aabbs = new AABB[getCollisionBoxSizes().length];
        for (int i = 0; i < getCollisionBoxSizes().length; i++) {
            aabbs[i] = new AABB(getCollisionBoxSizes()[i]);
            aabbs[i].move(x, y, z);
        }
        return aabbs;
    }

    public HitBox<Entity, IWorld>[] getSelectionBox(long x, long y, long z, HittableObject<Entity, IWorld> access) {
        HitBox<Entity, IWorld>[] hits = new HitBox[getSelectionBoxSizes().length];
        for (int i = 0; i < getSelectionBoxSizes().length; i++) {
            AABB aabb = getCollisionBoxSizes()[i];
            hits[i] = new HitBox<>(new AABB(x + aabb.x0, y + aabb.y0, z + aabb.z0, x + aabb.x1, y + aabb.y1, z + aabb.z1), access, new Vector3d(x, y, z));
        }
        return hits;
    }

    public void onInteract(Entity from, IWorld world, long x, long y, long z, byte f) {
        //todo:use inventory
        Vector3<Long> pos = EnumFacing.findNear(x, y, z, 1, f);
        if (world.isFree(ContentRegistries.BLOCK.get(from.getSelectBlock()).getCollisionBox(pos.x(), pos.y(), pos.z()))) {
            world.getBlockAccess(pos.x(), pos.y(), pos.z()).setBlockID(from.getSelectBlock(), true);
        }
    }

    public void onHit(Entity from, IWorld world, long x, long y, long z, byte f) {
        world.getBlockAccess(x, y, z).setBlockID("cubecraft:air", true);
    }

    public BlockState defaultState(long x, long y, long z) {
        return new BlockState(this.getID(), (byte) 0, (byte) 0).setX(x).setY(y).setZ(z);
    }

    public abstract int light();

    public boolean queryBoolean(String s, IBlockAccess blockAccess) {
        return switch (s) {
            case "face_top" -> blockAccess.getBlockFacing() == EnumFacing.Up;
            case "face_bottom" -> blockAccess.getBlockFacing() == EnumFacing.Down;
            case "face_left" -> blockAccess.getBlockFacing() == EnumFacing.West;
            case "face_right" -> blockAccess.getBlockFacing() == EnumFacing.East;
            case "face_front" -> blockAccess.getBlockFacing() == EnumFacing.South;
            case "face_back" -> blockAccess.getBlockFacing() == EnumFacing.North;
            case "default" -> true;
            default -> false;
        };
    }

    public boolean isLiquid() {
        return Arrays.asList(this.getTags()).contains("cubecraft:liquid");
    }
}