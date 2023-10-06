package ink.flybird.cubecraft.event.world;

import ink.flybird.cubecraft.level.Level;
import ink.flybird.cubecraft.world.IWorld;
import ink.flybird.cubecraft.world.entity.Entity;

public final class EntityLeaveLevelEvent extends WorldEvent{
    private final Entity entity;
    private final String reason;

    public EntityLeaveLevelEvent(Level level, IWorld world, Entity entity, String reason) {
        super(level, world);
        this.entity = entity;
        this.reason = reason;
    }

    public Entity getEntity() {
        return entity;
    }

    public String getReason() {
        return reason;
    }
}
