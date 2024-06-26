package net.cubecraft.server.internal.registries;

import net.cubecraft.util.setting.SettingItemRegistry;
import net.cubecraft.util.setting.item.IntegerSettingItem;

public interface ServerSettingRegistries {
    @SettingItemRegistry
    IntegerSettingItem CHUNK_LOAD_THREAD = new IntegerSettingItem("chunk_service","chunk_load_thread", 4);

    @SettingItemRegistry
    IntegerSettingItem CHUNK_SAVE_THREAD = new IntegerSettingItem("chunk_service","chunk_save_thread",  4);
}
