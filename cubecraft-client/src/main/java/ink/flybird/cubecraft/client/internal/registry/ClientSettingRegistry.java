package ink.flybird.cubecraft.client.internal.registry;

import ink.flybird.cubecraft.client.control.InputSettingItem;
import ink.flybird.cubecraft.util.setting.SettingItemRegistry;
import ink.flybird.cubecraft.util.setting.item.BooleanSettingItem;
import ink.flybird.cubecraft.util.setting.item.IntegerSettingItem;

public interface ClientSettingRegistry {
    @SettingItemRegistry
    IntegerSettingItem MAX_FPS = new IntegerSettingItem("render", "max_fps", 240);

    @SettingItemRegistry
    BooleanSettingItem V_SYNC = new BooleanSettingItem("render", "vsync", true);

    @SettingItemRegistry
    BooleanSettingItem FULL_SCREEN = new BooleanSettingItem("render", "fullscreen", false);

    @SettingItemRegistry
    IntegerSettingItem FXAA = new IntegerSettingItem("render", "fxaa", 0);


    @SettingItemRegistry
    BooleanSettingItem CHUNK_USE_AO = new BooleanSettingItem("terrain_renderer", "use_ao", true);

    @SettingItemRegistry
    BooleanSettingItem CHUNK_CLASSIC_LIGHTING = new BooleanSettingItem("terrain_renderer", "classic_lighting", true);

    @SettingItemRegistry
    IntegerSettingItem CHUNK_RENDER_DISTANCE = new IntegerSettingItem("terrain_renderer", "distance", 12);

    @SettingItemRegistry
    BooleanSettingItem CHUNK_USE_VBO = new BooleanSettingItem("terrain_renderer", "use_vbo", true);

    @SettingItemRegistry
    BooleanSettingItem CHUNK_FIX_DISTANCE = new BooleanSettingItem("terrain_renderer", "distance_fix", true);

    @SettingItemRegistry
    IntegerSettingItem CHUNK_UPDATE_THREAD = new IntegerSettingItem("terrain_renderer","update_thread",1);
}
