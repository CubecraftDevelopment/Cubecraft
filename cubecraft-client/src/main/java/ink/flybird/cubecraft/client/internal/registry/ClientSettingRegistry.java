package ink.flybird.cubecraft.client.internal.registry;

import io.flybird.cubecraft.util.SettingItemRegistry;
import io.flybird.cubecraft.util.setting.BooleanSettingItem;
import io.flybird.cubecraft.util.setting.IntegerSettingItem;

public interface ClientSettingRegistry {
    @SettingItemRegistry
    IntegerSettingItem MAX_FPS=new IntegerSettingItem("render","max_fps",240);

    @SettingItemRegistry
    BooleanSettingItem V_SYNC=new BooleanSettingItem("render","vsync",true);

    @SettingItemRegistry
    BooleanSettingItem FULL_SCREEN=new BooleanSettingItem("render","fullscreen",false);

    @SettingItemRegistry
    IntegerSettingItem FXAA=new IntegerSettingItem("render","fxaa",0);


    @SettingItemRegistry
    IntegerSettingItem CHUNK_RENDER_DISTANCE=new IntegerSettingItem("terrain_renderer","distance",12);
}
