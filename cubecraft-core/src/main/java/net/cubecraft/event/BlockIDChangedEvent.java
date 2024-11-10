package net.cubecraft.event;

import net.cubecraft.world.World;

public record BlockIDChangedEvent(World world, long x, long y, long z, int old, int id){

}
