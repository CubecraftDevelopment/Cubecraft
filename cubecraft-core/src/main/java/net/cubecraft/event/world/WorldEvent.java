package net.cubecraft.event.world;

import net.cubecraft.level.Level;
import net.cubecraft.world.IWorld;

public abstract class WorldEvent {
    private final Level level;
    private final IWorld world;

    protected WorldEvent(Level level, IWorld world) {
        this.level = level;
        this.world = world;
    }

    public Level getLevel() {
        return level;
    }

    public IWorld getWorld() {
        return world;
    }
}
