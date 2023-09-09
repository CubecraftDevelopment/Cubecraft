package ink.flybird.cubecraft.client.world;

import ink.flybird.cubecraft.client.CubecraftClient;
import io.flybird.cubecraft.internal.network.packet.PacketChunkGet;
import io.flybird.cubecraft.level.Level;
import io.flybird.cubecraft.world.IWorld;
import io.flybird.cubecraft.world.access.ChunkLoadAccess;
import io.flybird.cubecraft.world.chunk.*;
import io.flybird.cubecraft.world.entity.Entity;

import java.util.ConcurrentModificationException;
import java.util.Iterator;

public class ClientWorld extends IWorld {
    private final CubecraftClient client;

    public ClientWorld(String id, Level level, CubecraftClient client) {
        super(id, level);
        this.client = client;
    }


    @Override
    public void loadChunk(ChunkPos p, ChunkLoadTicket ticket) {
        if (this.getChunk(p) == null) {
            this.client.getClientIO().sendPacket(new PacketChunkGet(p, this.getID()));
            return;
        }
        this.getChunk(p).addTicket(ticket);
    }


    @Override
    public void tick() {
        super.tick();
        Iterator<Entity> it2 = this.entities.values().iterator();
        while (it2.hasNext()) {
            Entity e = it2.next();
            if (e == this.client.getPlayer()) {
                e.tick();
                ChunkLoadAccess.loadChunkRange(this, ChunkPos.fromWorldPos((long) e.x, (long) e.z), client.getGameSetting().getValueAsInt("client.world.simulation_distance", 5), new ChunkLoadTicket(ChunkLoadLevel.Entity_TICKING, 10));
            } else {
                WorldChunk c = this.getChunk(ChunkPos.fromWorldPos((long) e.x, (long) e.z));
                if (!c.task.shouldProcess(ChunkLoadTaskType.BLOCK_ENTITY_TICK)) {
                    it2.remove();
                }
            }
        }
        Iterator<WorldChunk> it = this.chunks.map.values().iterator();
        try {
            while (it.hasNext()) {
                WorldChunk chunk = it.next();
            }
        } catch (ConcurrentModificationException ignored) {
        }
    }
}
