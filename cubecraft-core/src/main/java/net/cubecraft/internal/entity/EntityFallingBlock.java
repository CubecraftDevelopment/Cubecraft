package net.cubecraft.internal.entity;

import net.cubecraft.ContentRegistries;
import net.cubecraft.world.IWorld;
import me.gb2022.commons.math.AABB;
import net.cubecraft.world.entity.EntityNonLiving;

public class EntityFallingBlock extends EntityNonLiving {
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

    public void setFallingID(String fallingID) {
        this.fallingID = fallingID;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.isOnGround()) {
            //this.world.removeEntity(this);
            //this.world.getBlockAccess((long) this.x, (long) this.y, (long) this.z).setBlockID(this.fallingID, false);
        }
    }
}
