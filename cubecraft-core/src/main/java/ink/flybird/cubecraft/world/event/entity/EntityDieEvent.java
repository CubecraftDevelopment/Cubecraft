package ink.flybird.cubecraft.world.event.entity;

import ink.flybird.cubecraft.world.entity.Entity;
import org.joml.Vector3d;

public record EntityDieEvent(Entity e, Vector3d diePos){
}
