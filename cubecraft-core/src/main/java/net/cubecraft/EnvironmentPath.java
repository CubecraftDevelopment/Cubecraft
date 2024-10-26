package net.cubecraft;

import java.io.File;

public interface EnvironmentPath {
    String GAME_FOLDER = System.getProperty("user.dir").replace("\\", "/");
    String MOD_FOLDER = GAME_FOLDER + "/mod";
    String CONFIG_FOLDER = GAME_FOLDER + "/config";
    String CACHE_FOLDER = GAME_FOLDER + "/cache";
    String RESOURCE_PACK_FOLDER = GAME_FOLDER + "/resource_pack";
    String SHADER_PACK_FOLDER = GAME_FOLDER + "/shader_pack";
    String LOG_FOLDER = GAME_FOLDER + "/log";
    String SAVE_FOLDER = GAME_FOLDER + "/save";
    String NATIVE_FOLDER = GAME_FOLDER + "/cache/native";

    static File getGameFolder() {
        return new File(GAME_FOLDER);
    }

    static File getModFolder() {
        return new File(MOD_FOLDER);
    }

    static File getConfigFolder() {
        return new File(CONFIG_FOLDER);
    }

    static File getResourcePackFolder() {
        return new File(RESOURCE_PACK_FOLDER);
    }

    static File getShaderPackFolder() {
        return new File(SHADER_PACK_FOLDER);
    }

    static File getLogFolder() {
        return new File(LOG_FOLDER);
    }

    static File getSaveFolder() {
        return new File(SAVE_FOLDER);
    }

    static File getNativeFolder() {
        return new File(NATIVE_FOLDER);
    }

    static void allCreateFolder() {
        getModFolder().mkdirs();
        getConfigFolder().mkdirs();
        getLogFolder().mkdirs();
        getSaveFolder().mkdirs();
        getResourcePackFolder().mkdirs();
        getShaderPackFolder().mkdirs();
        getNativeFolder().mkdirs();
    }

    static File getChunkDBFile(String sid) {
        String path="%s/%s/%s".formatted(SAVE_FOLDER, sid, "chunks");
        File f = new File(path);
        if (!f.exists()) {
            f.mkdirs();
        }
        return f;
    }

    static File getConfigFile(String name) {
        return new File(CONFIG_FOLDER + "/" + name);
    }

    static File getEntityDBFile(String sid) {
        String path="%s/%s/%s".formatted(SAVE_FOLDER, sid, "entities");
        File f = new File(path);
        if (!f.exists()) {
            f.mkdirs();
        }
        return f;
    }
}
