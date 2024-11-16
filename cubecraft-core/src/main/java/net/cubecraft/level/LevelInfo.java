package net.cubecraft.level;

import me.gb2022.commons.nbt.NBT;
import me.gb2022.commons.nbt.NBTTagCompound;
import net.cubecraft.EnvironmentPath;
import net.cubecraft.world.WorldFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Random;

public final class LevelInfo {
    public static final Logger LOGGER = LogManager.getLogger("LevelInfo");

    private final String folderPath;
    private final NBTTagCompound data;

    public LevelInfo(String folderPath, NBTTagCompound data) {
        this.folderPath = folderPath;
        this.data = data;
    }

    public static String allocateFolder(String savesFolderPath, String levelName) {
        var folder = savesFolderPath + "/" + levelName;

        if (!new File(folder).exists()) {
            return folder;
        }

        var aliasId = 1;

        while (true) {
            var folderName = folder + "(" + aliasId + ")";

            if (!new File(folderName).exists()) {
                return folderName;
            }

            aliasId++;
        }
    }

    public static LevelInfo create(String savesFolderPath, NBTTagCompound properties) throws IOException {
        var folder = allocateFolder(savesFolderPath, properties.getString("level_name"));
        var info = new LevelInfo(folder, properties);


        var data = info.getData();

        data.setLong("created_time", System.currentTimeMillis());
        data.setLong("last_play_time", System.currentTimeMillis());

        if (!data.hasKey("seed")) {
            data.setLong("seed", new Random().nextLong());
        }

        if (!data.hasKey("world_type")) {
            data.setString("world_type", "cubecraft:default");
        }

        info.save();

        return info;
    }

    public static LevelInfo open(String folder) throws IOException {
        var dataFile = new File(folder + "/level.dat");

        if (!dataFile.exists()) {
            throw new IllegalArgumentException("level.dat does not exist: " + dataFile);
        }

        try (var fis = new FileInputStream(dataFile)) {
            return new LevelInfo(folder, (NBTTagCompound) NBT.readZipped(fis));
        }
    }

    public static LevelInfo openOrElseCreate(String folder) throws IOException {
        var dataFile = new File(folder + "/level.dat");

        if (!dataFile.exists()) {
            return create(folder, new NBTTagCompound());
        }

        return open(folder);
    }


    public void save() throws IOException {
        var dataFile = new File(this.folderPath + "/level.dat");

        if (dataFile.getParentFile().mkdirs()) {
            LOGGER.info("created level folder: {}", this.folderPath);
        }
        if (dataFile.createNewFile()) {
            LOGGER.info("created level file: {}", dataFile);
        }
    }

    public NBTTagCompound getData() {
        return data;
    }

    public String getFolderPath() {//todo: use relative path 4 db relocate
        return folderPath;
    }

    public Level createLevel(WorldFactory factory) {
        return new Level(this, factory);
    }

    public String getLevelName() {
        return this.data.getString("level_name");
    }

    public Date getCreatedTime() {
        return new Date(this.data.getLong("created_time"));
    }

    public Date getLastPlayTime() {
        return new Date(this.data.getLong("last_play_time"));
    }

    public long getSeed() {
        return this.data.getLong("seed");
    }

    public void updateLastPlayTime() {
        this.data.setLong("last_play_time", System.currentTimeMillis());
    }

    public File getFolder() {
        return new File(EnvironmentPath.getSaveFolder() + "/" + this.getLevelName());
    }
}