package net.cubecraft.server.internal.registries;

import net.cubecraft.util.setting.SettingItemRegistry;
import net.cubecraft.util.setting.item.IntSetting;

public interface ServerSettingRegistries {
    @SettingItemRegistry
    IntSetting CHUNK_LOAD_THREAD = new IntSetting("chunk_service", "chunk_load_thread", 4);

    @SettingItemRegistry
    IntSetting CHUNK_SAVE_THREAD = new IntSetting("chunk_service", "chunk_save_thread", 4);
}
