package net.cubecraft.level;

import me.gb2022.commons.container.MultiMap;
import me.gb2022.commons.event.SimpleEventBus;
import net.cubecraft.event.world.EntityJoinLevelEvent;
import net.cubecraft.event.world.EntityLeaveLevelEvent;
import net.cubecraft.internal.entity.EntityPlayer;
import net.cubecraft.world.World;
import net.cubecraft.world.WorldContext;
import net.cubecraft.world.WorldFactory;
import net.cubecraft.world.entity.Entity;
import net.cubecraft.world.storage.PersistentEntityHolder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

public class Level {
    public static final Logger LOGGER = LogManager.getLogger("Level");

    public final MultiMap<String, World> worlds = new MultiMap<>();
    private final SimpleEventBus eventBus = new SimpleEventBus();
    private final LevelInfo levelInfo;
    private PersistentEntityHolder persistentEntityHolder;

    public Level(LevelInfo info, WorldFactory worldFactory) {
        this.levelInfo = info;

        for (String id : new String[]{"cubecraft:overworld"}) {
            this.worlds.put(id, worldFactory.create(id, this));
        }
    }

    public Location getSpawnPoint() {
        var w = worlds.get("cubecraft:overworld");

        var x = 0;
        var z = 0;

        var h = w.getChunkSafely(x, z).getHighestBlockAt(0, 0);

        return new Location("cubecraft:overworld", x + 0.5, h + 1, z + 0.5, 0, 0, 0);
    }

    public LevelInfo getLevelInfo() {
        return this.levelInfo;
    }

    public MultiMap<String, World> getWorlds() {
        return worlds;
    }

    public World getDimension(String worldID) {
        return this.worlds.get(worldID);
    }

    public int getWorldCount() {
        return this.worlds.size();
    }

    public Location getLocation(Entity entity) {
        if (entity == null) {
            return getSpawnPoint();
        }
        return getSpawnPoint();
    }

    public World getWorld(String id) {
        return this.worlds.get(id);
    }

    public SimpleEventBus getEventBus() {
        return eventBus;
    }

    public WorldContext join(@NotNull Entity entity) {
        Location entitySpawnLocation = this.getLocation(entity);

        if (!this.persistentEntityHolder.load(entity)) {
            entity.setWorld(entitySpawnLocation.getWorld(this));
            entity.setPos(entitySpawnLocation.getPosition());
            entity.setRotation(entitySpawnLocation.getRotation());
        }

        if (entity instanceof EntityPlayer player) {
            LOGGER.info(
                    "player {} joined in {} at {} {} {} with uuid {}",
                    player.getSession().getName(),
                    entity.getWorld().getId(),
                    entity.x,
                    entity.y,
                    entity.z,
                    entity.getUuid()
            );
        }

        entitySpawnLocation.getWorld(this).addEntity(entity);
        this.eventBus.callEvent(new EntityJoinLevelEvent(this, entitySpawnLocation.getWorld(this), entity));

        return new WorldContext(this, entity.getWorld(), entity);
    }

    public void leave(@NotNull Entity entity, String reason) {
        World world = entity.getWorld();
        world.removeEntity(entity);

        if (entity.shouldSave()) {
            this.persistentEntityHolder.save(entity);
        }

        this.getEventBus().callEvent(new EntityLeaveLevelEvent(this, world, entity, reason));
    }

    public void save() {
        for (String id : this.worlds.keySet()) {
            try {
                this.worlds.get(id).save();
            } catch (Throwable t) {
                LOGGER.error("failed to save level {}", id);
                LOGGER.catching(t);
            }
        }
    }

    public PersistentEntityHolder getPersistentEntityHolder() {
        return persistentEntityHolder;
    }

    public void setPersistentEntityHolder(PersistentEntityHolder persistentEntityHolder) {
        this.persistentEntityHolder = persistentEntityHolder;
    }
}
