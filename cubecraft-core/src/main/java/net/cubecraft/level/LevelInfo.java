package net.cubecraft.level;

import net.cubecraft.EnvironmentPath;
import me.gb2022.commons.nbt.NBT;
import me.gb2022.commons.nbt.NBTTagCompound;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.io.*;
import java.util.Date;
import java.util.Properties;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class LevelInfo {
    private static final Logger LOGGER = LogManager.getLogger("level_info");

    private final NBTTagCompound tag;
    private final Properties properties = new Properties();

    public LevelInfo(NBTTagCompound tag) {
        this.tag = tag;
    }

    public LevelInfo() {
        this.tag = new NBTTagCompound();
    }


    public static LevelInfo create(String name, long seed) {
        LevelInfo levelInfo = load(name);

        if (levelInfo == null) {
            levelInfo = new LevelInfo();
        }

        levelInfo.setString("level_name", name);
        levelInfo.setLong("seed", seed);
        levelInfo.setLong("created_time", System.currentTimeMillis());
        levelInfo.updateLastPlayTime();

        save(levelInfo);

        return levelInfo;
    }

    public static LevelInfo load(String levelName) {
        File f = getLevelInfoFile(levelName);
        return load(f);
    }

    public static LevelInfo load(File worldFolder) {
        return loadFile(new File(worldFolder.getAbsolutePath() + "/level.dat"));
    }

    public static LevelInfo loadFile(File levelFile) {
        LevelInfo levelInfo = null;
        if (!levelFile.exists()) {
            return null;
        }
        try {
            FileInputStream fis = new FileInputStream(levelFile);
            GZIPInputStream zis = new GZIPInputStream(fis);
            DataInputStream dis = new DataInputStream(zis);

            levelInfo = new LevelInfo((NBTTagCompound) NBT.read(dis));

            dis.close();
            zis.close();
            fis.close();
        } catch (IOException e) {
            LOGGER.error(e);
        }
        return levelInfo;
    }

    public static void save(LevelInfo levelInfo) {
        File f = getLevelInfoFile(levelInfo.getLevelName());
        try {
            f.getParentFile().mkdirs();
            f.createNewFile();

            FileOutputStream fos = new FileOutputStream(f);
            GZIPOutputStream zos = new GZIPOutputStream(fos);
            DataOutputStream dos = new DataOutputStream(zos);

            NBT.write(levelInfo.tag, dos);

            dos.close();
            zos.close();
            fos.close();
        } catch (IOException e) {
            LOGGER.error(e);
        }
    }

    public static File getLevelInfoFile(String levelName) {
        String worldFolder = EnvironmentPath.SAVE_FOLDER;
        return new File(worldFolder + "/" + levelName + "/level.dat");
    }


    public byte getByte(String id) {
        return this.tag.getByte(id);
    }

    public short getShort(String id) {
        return this.tag.getShort(id);
    }

    public int getInt(String id) {
        return this.tag.getInteger(id);
    }

    public long getLong(String id) {
        return this.tag.getLong(id);
    }

    public float getFloat(String id) {
        return this.tag.getFloat(id);
    }

    public double getDouble(String id) {
        return this.tag.getDouble(id);
    }

    private String getString(String id) {
        return this.tag.getString(id);
    }

    public NBTTagCompound getNBT(String id) {
        return this.tag.getCompoundTag(id);
    }


    public void setByte(String id, byte value) {
        this.tag.setByte(id, value);
    }

    public void setShort(String id, short value) {
        this.tag.setShort(id, value);
    }

    public void setInt(String id, int value) {
        this.tag.setInteger(id, value);
    }

    public void setLong(String id, long value) {
        this.tag.setLong(id, value);
    }

    public void setFloat(String id, float value) {
        this.tag.setFloat(id, value);
    }

    public void setDouble(String id, double value) {
        this.tag.setDouble(id, value);
    }

    private void setString(String id, String value) {
        this.tag.setString(id, value);
    }

    public void setNBT(String id, NBTTagCompound value) {
        this.tag.setCompoundTag(id, value);
    }


    public String getLevelName() {
        return this.getString("level_name");
    }

    public Date getCreatedTime() {
        return new Date(this.getLong("created_time"));
    }

    public Date getLastPlayTime() {
        return new Date(this.getLong("last_play_time"));
    }

    public long getSeed() {
        return this.getLong("seed");
    }

    public void updateLastPlayTime() {
        this.setLong("last_play_time", System.currentTimeMillis());
    }

    public NBTTagCompound getTag() {
        return this.tag;
    }

    public File getFolder() {
        return new File(EnvironmentPath.getSaveFolder() + "/" + this.getLevelName());
    }
}