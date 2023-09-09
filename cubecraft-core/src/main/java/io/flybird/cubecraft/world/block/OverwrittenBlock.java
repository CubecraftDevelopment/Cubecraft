package io.flybird.cubecraft.world.block;

import ink.flybird.fcommon.math.AABB;

public class OverwrittenBlock extends Block {

    private final Block behavior;
    final String id;

    /**
     * this is an override block. if you want to change any attribute from parent,just override it.
     * @param material parent
     */
    public OverwrittenBlock(String id,Block material){
        super(id);
        this.id=id;
        this.behavior=material;
    }

    @Override
    public EnumFacing[] getEnabledFacings() {
        return behavior.getEnabledFacings();
    }

    @Override
    public AABB[] getCollisionBoxSizes() {
        return behavior.getCollisionBoxSizes();
    }

    @Override
    public AABB[] getSelectionBoxSizes() {
        return behavior.getSelectionBoxSizes();
    }

    @Override
    public float getResistance() {
        return behavior.getResistance();
    }

    @Override
    public float getDensity() {
        return behavior.getDensity();
    }

    @Override
    public float getHardNess() {
        return behavior.getHardNess();
    }

    @Override
    public String[] getTags() {
        return behavior.getTags();
    }

    @Override
    public int light() {
        return 0;
    }

    @Override
    public boolean isSolid() {
        return behavior.isSolid();
    }

    @Override
    public int opacity() {
        return behavior.opacity();
    }

    @Override
    public String getID() {
        return id;
    }
}
