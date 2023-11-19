package net.cubecraft.server.internal.registries;

import net.cubecraft.util.setting.SettingItemRegistry;
import net.cubecraft.util.setting.item.IntegerSettingItem;

public interface ServerSettingRegistries {
    @SettingItemRegistry
    IntegerSettingItem CHUNK_LOAD_THREAD = new IntegerSettingItem("chunk_load_thread", "chunk_service", 3);

    @SettingItemRegistry
    IntegerSettingItem CHUNK_SAVE_THREAD = new IntegerSettingItem("chunk_save_thread", "chunk_service", 3);
}
