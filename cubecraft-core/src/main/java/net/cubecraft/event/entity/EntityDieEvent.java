package net.cubecraft.event.entity;

import net.cubecraft.world.entity.Entity;
import org.joml.Vector3d;

public record EntityDieEvent(Entity e, Vector3d diePos){
}
