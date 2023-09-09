package io.flybird.cubecraft.world.event;

import io.flybird.cubecraft.world.IWorld;

public record BlockIDChangedEvent(IWorld world,long x,long y,long z,String old, String id){
}
