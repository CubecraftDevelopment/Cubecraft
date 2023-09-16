package ink.flybird.cubecraft.world.event;

import ink.flybird.cubecraft.world.IWorld;

public record BlockIDChangedEvent(IWorld world, long x, long y, long z, String old, String id){
}
