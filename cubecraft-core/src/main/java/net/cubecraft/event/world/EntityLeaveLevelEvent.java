package net.cubecraft.event.world;

import net.cubecraft.level.Level;
import net.cubecraft.world.World;
import net.cubecraft.world.entity.Entity;

public final class EntityLeaveLevelEvent extends WorldEvent{
    private final Entity entity;
    private final String reason;

    public EntityLeaveLevelEvent(Level level, World world, Entity entity, String reason) {
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
