package ink.flybird.cubecraft.internal.entity;

import ink.flybird.cubecraft.register.ContentRegistries;
import ink.flybird.cubecraft.world.IWorld;
import ink.flybird.cubecraft.world.entity.Entity;
import ink.flybird.fcommon.math.AABB;
import ink.flybird.fcommon.math.HitBox;

public class EntityFallingBlock extends Entity {
    private String fallingID;
    //todo:take block meta

    public EntityFallingBlock(IWorld world, String fallingID) {
        super(world);
        this.fallingID = fallingID;
    }

    @Override
    public AABB getCollisionBoxSize() {
        return ContentRegistries.BLOCK.get(this.fallingID).getCollisionBoxSizes()[0];
    }

    @Override
    public HitBox<Entity, IWorld>[] getSelectionBoxes() {
        return new HitBox[0];
    }

    public void setFallingID(String fallingID) {
        this.fallingID = fallingID;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.onGround) {
            //this.world.removeEntity(this);
            //this.world.getBlockAccess((long) this.x, (long) this.y, (long) this.z).setBlockID(this.fallingID, false);
        }
    }
}
