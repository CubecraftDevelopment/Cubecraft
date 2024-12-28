package net.cubecraft.event.world;

import net.cubecraft.level.Level;
import net.cubecraft.world.World;

public abstract class WorldEvent {
    private final Level level;
    private final World world;

    public WorldEvent(Level level, World world) {
        this.level = level;
        this.world = world;
    }

    public final Level getLevel() {
        return level;
    }

    public final World getWorld() {
        return world;
    }
}
