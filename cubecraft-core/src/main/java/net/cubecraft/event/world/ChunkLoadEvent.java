package net.cubecraft.event.world;

import net.cubecraft.world.chunk.task.ChunkLoadTicket;
import net.cubecraft.world.chunk.pos.ChunkPos;
import net.cubecraft.world.entity.Entity;

public record ChunkLoadEvent(Entity entity, ChunkPos pos, ChunkLoadTicket ticket){
}
