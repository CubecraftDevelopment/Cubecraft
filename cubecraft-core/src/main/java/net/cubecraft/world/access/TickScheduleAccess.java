package net.cubecraft.world.access;

import net.cubecraft.world.World;

public interface TickScheduleAccess {
    static void setNeighborScheduleTick(World world, long x, long y, long z, int time) {
        world.setTickSchedule(x, y - 1, z, time);
        world.setTickSchedule(x, y + 1, z, time);
        world.setTickSchedule(x - 1, y, z, time);
        world.setTickSchedule(x + 1, y, z, time);
        world.setTickSchedule(x, y, z - 1, time);
        world.setTickSchedule(x, y, z + 1, time);
    }

    static void setNeighborTick(World world, long x, long y, long z) {
        world.setTick(x, y - 1, z);
        world.setTick(x, y + 1, z);
        world.setTick(x - 1, y, z);
        world.setTick(x + 1, y, z);
        world.setTick(x, y, z - 1);
        world.setTick(x, y, z + 1);
    }
}
