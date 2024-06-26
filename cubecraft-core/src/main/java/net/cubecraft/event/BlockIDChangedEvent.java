package net.cubecraft.event;

import net.cubecraft.world.IWorld;

public record BlockIDChangedEvent(IWorld world, long x, long y, long z, String old, String id){

}
