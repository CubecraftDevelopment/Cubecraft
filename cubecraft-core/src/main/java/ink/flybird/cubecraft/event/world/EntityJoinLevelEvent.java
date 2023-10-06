package ink.flybird.cubecraft.event.world;

import ink.flybird.cubecraft.level.Level;
import ink.flybird.cubecraft.world.IWorld;
import ink.flybird.cubecraft.world.entity.Entity;

public final class EntityJoinLevelEvent extends WorldEvent{
    private final Entity entity;

    public EntityJoinLevelEvent(Level level, IWorld world, Entity entity) {
        super(level, world);
        this.entity = entity;
    }

    public Entity getEntity() {
        return entity;
    }
}
