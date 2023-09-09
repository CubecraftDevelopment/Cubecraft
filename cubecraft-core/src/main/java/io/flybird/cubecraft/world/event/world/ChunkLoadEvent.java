package io.flybird.cubecraft.world.event.world;

import io.flybird.cubecraft.world.chunk.ChunkLoadTicket;
import io.flybird.cubecraft.world.chunk.ChunkPos;
import io.flybird.cubecraft.world.entity.Entity;

public record ChunkLoadEvent(Entity entity, ChunkPos pos, ChunkLoadTicket ticket){
}
