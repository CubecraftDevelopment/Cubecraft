package net.cubecraft.world.entity;

import ink.flybird.fcommon.math.AABB;
import ink.flybird.fcommon.math.hitting.HitBox;
import net.cubecraft.world.IWorld;
import java.util.Collection;
import java.util.List;

public abstract class EntityNonLiving extends Entity {
    public EntityNonLiving(IWorld world) {
        super(world);
    }

    @Override
    public Collection<HitBox> getHitBox() {
        return List.of(new HitBox("default", this.getCollisionBox()));
    }

    @Override
    public AABB getCollisionBoxSize() {
        return new AABB(-0.3, 0, -0.3, 0.3, 1.75, 0.3);
    }
}
