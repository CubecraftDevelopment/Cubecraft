package ink.flybird.cubecraft.level;

import ink.flybird.cubecraft.ContentRegistries;
import ink.flybird.cubecraft.world.IWorld;
import ink.flybird.cubecraft.world.WorldFactory;
import ink.flybird.fcommon.file.FileUtil;
import ink.flybird.cubecraft.world.entity.EntityLocation;

import java.util.HashMap;

public class Level {
    public final HashMap<String, IWorld> dims = new HashMap<>();
    private final LevelInfo levelInfo;
    private final WorldFactory worldFactory;

    public Level(LevelInfo info, WorldFactory worldFactory) {
        this.worldFactory = worldFactory;
        for (String id : ContentRegistries.DIMENSION.idList()) {
            this.dims.put(id,this.worldFactory.create(id,this));
        }
        this.levelInfo = info;
    }

    public String getName() {
        return this.levelInfo.getProperty(LevelInfo.NAME);
    }

    public void createLevelFolder() {
        FileUtil.createFileRelative("/data/saves/%s/level.nbt".formatted(this.getName()));
        for (IWorld world : this.dims.values()) {
            FileUtil.createFileRelative("/data/saves/%s/%s/chunk.ldb".formatted(this.getName(), world.getID().replace(":", "_")));
            FileUtil.createFileRelative("/data/saves/%s/%s/entity.ldb".formatted(this.getName(), world.getID().replace(":", "_")));
        }
    }

    public EntityLocation getSpawnPoint(String uid) {
        return new EntityLocation(0, 128, 0, 0, 0, 0, "cubecraft:overworld");
    }

    public LevelInfo getInfo() {
        return this.levelInfo;
    }

    public HashMap<String, IWorld> getDims() {
        return dims;
    }

    public IWorld getDimension(String worldID) {
        return this.dims.get(worldID);
    }

    public WorldFactory getWorldFactory() {
        return worldFactory;
    }

    public int getWorldCount() {
        return this.dims.size();
    }
}
