package ink.flybird.cubecraft.level;


import ink.flybird.fcommon.file.NBTBuilder;
import ink.flybird.fcommon.file.NBTDataIO;
import ink.flybird.fcommon.nbt.NBTTagCompound;

import java.io.*;
import java.util.Properties;

public class LevelInfo implements NBTDataIO {
    public static final String NAME = "info.name";
    public static final String CREATOR = "info.creator";
    public static final String DATE = "info.date";

    public static final String SEED = "world.seed";


    private final Properties properties = new Properties();

    public Properties getProperties() {
        return properties;
    }

    public LevelInfo() {

    }

    @Override
    public NBTTagCompound getData() {
        NBTTagCompound tag = new NBTTagCompound();
        this.properties.keySet().forEach(key -> tag.setString((String) key, getProperty((String) key)));
        return tag;
    }

    @Override
    public void setData(NBTTagCompound tag) {
        for (String key : tag.getTagMap().keySet()) {
            this.setProperty(key, tag.getString(key));
        }
    }

    public String getProperty(String path) {
        return this.properties.getProperty(path);
    }

    public void setProperty(String path, String value) {
        this.properties.setProperty(path, value);
    }


    public void load(InputStream inputStream) {
        try {
            this.properties.load(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void save(LevelInfo info, File folder) throws IOException {
        File f = new File(folder.getAbsolutePath() + "/level_info.dat");
        f.createNewFile();
        NBTBuilder.write(info.getData(), new DataOutputStream(new FileOutputStream(f)));
    }
}