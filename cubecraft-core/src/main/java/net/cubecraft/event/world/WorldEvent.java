package net.cubecraft.event.world;

import net.cubecraft.level.Level;
import net.cubecraft.world.World;

public abstract class WorldEvent {
    private final Level level;
    private final World world;

    protected WorldEvent(Level level, World world) {
        this.level = level;
        this.world = world;
    }

    public Level getLevel() {
        return level;
    }

    public World getWorld() {
        return world;
    }
}
