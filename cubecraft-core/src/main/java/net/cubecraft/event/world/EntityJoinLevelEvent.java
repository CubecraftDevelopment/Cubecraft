package net.cubecraft.event.world;

import net.cubecraft.level.Level;
import net.cubecraft.world.World;
import net.cubecraft.world.entity.Entity;

public final class EntityJoinLevelEvent extends WorldEvent{
    private final Entity entity;

    public EntityJoinLevelEvent(Level level, World world, Entity entity) {
        super(level, world);
        this.entity = entity;
    }

    public Entity getEntity() {
        return entity;
    }
}
