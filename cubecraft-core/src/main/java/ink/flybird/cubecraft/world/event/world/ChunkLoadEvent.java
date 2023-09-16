package ink.flybird.cubecraft.world.event.world;

import ink.flybird.cubecraft.world.chunk.ChunkLoadTicket;
import ink.flybird.cubecraft.world.chunk.ChunkPos;
import ink.flybird.cubecraft.world.entity.Entity;

public record ChunkLoadEvent(Entity entity, ChunkPos pos, ChunkLoadTicket ticket){
}
