package io.flybird.cubecraft.world.access;

import io.flybird.cubecraft.world.IWorld;

public interface TickScheduleAccess {
    static void setNeighborScheduleTick(IWorld world, long x, long y, long z, int time) {
        world.setTickSchedule(x, y - 1, z, time);
        world.setTickSchedule(x, y + 1, z, time);
        world.setTickSchedule(x - 1, y, z, time);
        world.setTickSchedule(x + 1, y, z, time);
        world.setTickSchedule(x, y, z - 1, time);
        world.setTickSchedule(x, y, z + 1, time);
    }

    static void setNeighborTick(IWorld world,long x, long y, long z) {
        world.setTick(x, y - 1, z);
        world.setTick(x, y + 1, z);
        world.setTick(x - 1, y, z);
        world.setTick(x + 1, y, z);
        world.setTick(x, y, z - 1);
        world.setTick(x, y, z + 1);
    }
}
