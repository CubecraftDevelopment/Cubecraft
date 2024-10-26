package net.cubecraft.world.entity;

import me.gb2022.commons.math.AABB;
import me.gb2022.commons.math.hitting.HitBox;
import net.cubecraft.world.World;
import java.util.Collection;
import java.util.List;

public abstract class EntityNonLiving extends Entity {
    public EntityNonLiving(World world) {
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
