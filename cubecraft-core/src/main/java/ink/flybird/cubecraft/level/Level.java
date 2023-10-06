package ink.flybird.cubecraft.level;

import ink.flybird.cubecraft.ContentRegistries;
import ink.flybird.cubecraft.event.world.EntityJoinLevelEvent;
import ink.flybird.cubecraft.event.world.EntityLeaveLevelEvent;
import ink.flybird.cubecraft.world.IWorld;
import ink.flybird.cubecraft.world.WorldFactory;
import ink.flybird.cubecraft.world.entity.Entity;
import ink.flybird.cubecraft.world.entity.EntityLocation;
import ink.flybird.fcommon.container.MultiMap;
import ink.flybird.fcommon.event.EventBus;
import ink.flybird.fcommon.event.SimpleEventBus;
import ink.flybird.fcommon.file.FileUtil;
import org.jetbrains.annotations.NotNull;

public class Level {
    public final MultiMap<String, IWorld> worlds = new MultiMap<>();

    private final EventBus eventBus = new SimpleEventBus();
    private final LevelInfo levelInfo;
    private final WorldFactory worldFactory;

    public Level(LevelInfo info, WorldFactory worldFactory) {
        this.worldFactory = worldFactory;
        for (String id : ContentRegistries.DIMENSION.idList()) {
            this.worlds.put(id, this.worldFactory.create(id, this));
        }
        this.levelInfo = info;
    }

    public void createLevelFolder() {
        String levelName = this.getLevelInfo().getLevelName();

        for (IWorld world : this.worlds.values()) {
            FileUtil.createFileRelative("/data/saves/%s/%s/chunk.ldb".formatted(levelName, world.getID().replace(":", "_")));
            FileUtil.createFileRelative("/data/saves/%s/%s/entity.ldb".formatted(levelName, world.getID().replace(":", "_")));
        }
    }

    public EntityLocation getSpawnPoint(String uid) {
        return new EntityLocation(0, 128, 0, 0, 0, 0, "cubecraft:overworld");
    }

    public LevelInfo getLevelInfo() {
        return this.levelInfo;
    }

    public MultiMap<String, IWorld> getWorlds() {
        return worlds;
    }

    public IWorld getDimension(String worldID) {
        return this.worlds.get(worldID);
    }

    public WorldFactory getWorldFactory() {
        return worldFactory;
    }

    public int getWorldCount() {
        return this.worlds.size();
    }

    public Location getLocation(Entity entity) {
        if (entity == null) {
            return new Location("cubecraft:overworld", 0, 128, 0, 0, 0, 0);
        }
        return new Location("cubecraft:overworld", 0, 512, 0, 0, 0, 0);
    }

    public IWorld getWorld(String id) {
        return this.worlds.get(id);
    }

    public EventBus getEventBus() {
        return eventBus;
    }


    public void join(@NotNull Entity entity) {
        Location entitySpawnLocation = this.getLocation(entity);
        IWorld world = entitySpawnLocation.getWorld(this);

        entity.setWorld(world);
        entity.setPos(entitySpawnLocation.getPosition());
        entity.setRotation(entitySpawnLocation.getRotation());

        world.addEntity(entity);
        this.eventBus.callEvent(new EntityJoinLevelEvent(this, world, entity));
    }

    public void leave(@NotNull Entity entity, String reason) {
        IWorld world = entity.getWorld();
        world.removeEntity(entity);
        this.getEventBus().callEvent(new EntityLeaveLevelEvent(this, world, entity, reason));
    }
}
