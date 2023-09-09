package io.flybird.cubecraft.level;

import io.flybird.cubecraft.register.EnvironmentPath;
import ink.flybird.fcommon.file.NBTBuilder;
import ink.flybird.fcommon.nbt.NBTTagCompound;

import java.io.*;

public interface LevelInfoFactory {
    static LevelInfo fromWorldFolder(File folder) {
        File f = new File(folder.getAbsolutePath() + "/level_info.dat");
        LevelInfo info = new LevelInfo();
        try {
            NBTTagCompound tag = (NBTTagCompound) NBTBuilder.read(new DataInputStream(new FileInputStream(f)));
            info.setData(tag);
        } catch (IOException e) {
            return null;
        }
        return info;
    }

    static LevelInfo fromSaveFolder(String worldFolderName) {
        return fromWorldFolder(new File(EnvironmentPath.SAVE_FOLDER + "/" + worldFolderName));
    }

    static LevelInfo create(String levelName) {
        LevelInfo info = LevelInfoFactory.fromSaveFolder(levelName);
        if (info != null) {
            return info;
        }
        info = new LevelInfo();
        try {
            info.load(new FileInputStream(EnvironmentPath.CONFIG_FOLDER + "/world_gen_setting.properties"));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        info.setProperty(LevelInfo.NAME, levelName);

        return info;
    }
}
