package net.cubecraft.world;

import net.cubecraft.internal.entity.EntityPlayer;
import net.cubecraft.level.Level;
import net.cubecraft.world.chunk.pos.ChunkPos;
import net.cubecraft.world.entity.Entity;

public final class WorldContext {
    private final Level level;
    private final World world;
    private final Entity entity;

    public WorldContext(Level level, World world, Entity entity) {
        this.level = level;
        this.world = world;
        this.entity = entity;
    }

    public WorldContext switchDimension(String dimension) {
        return new WorldContext(this.level, this.level.getDimension(dimension), this.entity);
    }

    public void leave() {
        this.world.getLevel().leave(this.entity, "left");
    }

    public void tick() {
        if (this.entity == null) {
            return;
        }
        if (this.world.getChunk(ChunkPos.fromEntity(this.entity)) == null) {
            return;
        }
        if (this.world.isClient()) {
            this.world.tick();
        }

        this.entity.tick();
    }

    public Level getLevel() {
        return level;
    }

    public World getWorld() {
        return world;
    }

    public EntityPlayer getPlayer() {
        return (EntityPlayer) entity;
    }

    public Entity getEntity() {
        return entity;
    }

    public void destroy() {
        this.level.leave(this.entity, "left");
    }
}
