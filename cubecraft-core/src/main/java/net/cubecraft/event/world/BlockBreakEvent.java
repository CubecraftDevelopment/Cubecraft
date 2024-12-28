package net.cubecraft.event.world;

import net.cubecraft.level.Level;
import net.cubecraft.world.World;

public final class BlockBreakEvent extends BlockEvent {
    private final int originalBlock;

    public BlockBreakEvent(Level level, World world, long x, long y, long z, int originalBlock) {
        super(level, world, x, y, z);
        this.originalBlock = originalBlock;
    }


    public int getOriginalBlock() {
        return originalBlock;
    }
}


