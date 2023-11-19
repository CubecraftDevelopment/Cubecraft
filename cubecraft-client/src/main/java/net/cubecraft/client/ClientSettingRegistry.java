package net.cubecraft.client;

import net.cubecraft.util.setting.SettingItemRegistry;
import net.cubecraft.util.setting.item.BooleanSettingItem;
import net.cubecraft.util.setting.item.IntegerSettingItem;

public interface ClientSettingRegistry {
    @SettingItemRegistry
    BooleanSettingItem CHUNK_USE_AO = new BooleanSettingItem("chunk_renderer", "use_ao", true);

    @SettingItemRegistry
    BooleanSettingItem CHUNK_CLASSIC_LIGHTING = new BooleanSettingItem("chunk_renderer", "classic_lighting", true);

    @SettingItemRegistry
    IntegerSettingItem CHUNK_RENDER_DISTANCE = new IntegerSettingItem("chunk_renderer", "distance", 12);

    @SettingItemRegistry
    BooleanSettingItem CHUNK_USE_VBO = new BooleanSettingItem("chunk_renderer", "use_vbo", true);

    @SettingItemRegistry
    BooleanSettingItem CHUNK_FIX_DISTANCE = new BooleanSettingItem("chunk_renderer", "distance_fix", true);

    @SettingItemRegistry
    IntegerSettingItem CHUNK_UPDATE_THREAD = new IntegerSettingItem("chunk_renderer", "update_thread", 1);

    @SettingItemRegistry
    IntegerSettingItem MAX_UPLOAD_COUNT = new IntegerSettingItem("chunk_renderer", "max_upload_count", 16);

    @SettingItemRegistry
    IntegerSettingItem MAX_RECEIVE_COUNT = new IntegerSettingItem("chunk_renderer", "max_receive_count", 512);

    @SettingItemRegistry
    BooleanSettingItem FORCE_REBUILD_NEAREST_CHUNK = new BooleanSettingItem("chunk_renderer", "force_rebuild_nearest_chunk", false);
}
