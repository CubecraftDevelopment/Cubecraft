package net.cubecraft.event.world;

import net.cubecraft.level.Level;
import net.cubecraft.world.World;

public abstract class BlockEvent extends WorldEvent {
    private final long x;
    private final long y;
    private final long z;

    public BlockEvent(Level level, World world, long x, long y, long z) {
        super(level, world);
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public final long getX() {
        return x;
    }

    public final long getY() {
        return y;
    }

    public final long getZ() {
        return z;
    }
}
