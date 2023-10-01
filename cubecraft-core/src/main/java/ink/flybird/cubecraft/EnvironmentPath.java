package ink.flybird.cubecraft;

import java.io.File;

public final class EnvironmentPath {
    public static final String GAME_FOLDER =System.getProperty("user.dir").replace("\\", "/");
    public static final String MOD_FOLDER = GAME_FOLDER +"/mod";
    public static final String CONFIG_FOLDER = GAME_FOLDER +"/config";
    public static final String CACHE_FOLDER = GAME_FOLDER +"/cache";
    public static final String RESOURCE_PACK_FOLDER = GAME_FOLDER+"/resource_pack";
    public static final String SHADER_PACK_FOLDER = GAME_FOLDER+"/shader_pack";
    public static final String LOG_FOLDER = GAME_FOLDER+"/log";
    public static final String SAVE_FOLDER = GAME_FOLDER+"/save";
    public static final String NATIVE_FOLDER = GAME_FOLDER+"/cache/native";

    public static File getGameFolder(){
        return new File(GAME_FOLDER);
    }

    public static File getModFolder(){
        return new File(MOD_FOLDER);
    }

    public static File getConfigFolder(){
        return new File(CONFIG_FOLDER);
    }

    public static File getResourcePackFolder(){
        return new File(RESOURCE_PACK_FOLDER);
    }

    public static File getShaderPackFolder(){
        return new File(SHADER_PACK_FOLDER);
    }

    public static File getLogFolder(){
        return new File(LOG_FOLDER);
    }

    public static File getSaveFolder(){
        return new File(SAVE_FOLDER);
    }

    public static File getNativeFolder(){
        return new File(NATIVE_FOLDER);
    }

    public static void allCreateFolder() {
        getModFolder().mkdirs();
        getConfigFolder().mkdirs();
        getLogFolder().mkdirs();
        getSaveFolder().mkdirs();
        getResourcePackFolder().mkdirs();
        getShaderPackFolder().mkdirs();
        getNativeFolder().mkdirs();
    }

    public static File getChunkDBFile(String wid,String sid) {
        return new File("%s/%s/%s/%s.leveldb".formatted(SAVE_FOLDER,sid,"chunks",wid));
    }

    public static File getConfigFile(String name){
        return new File(CONFIG_FOLDER+"/"+name);
    }
}
