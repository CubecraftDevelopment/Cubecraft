package ink.flybird.cubecraft.server.internal.registries;

import ink.flybird.cubecraft.util.setting.SettingItemRegistry;
import ink.flybird.cubecraft.util.setting.item.IntegerSettingItem;

public interface ServerSettingRegistries {
    @SettingItemRegistry
    IntegerSettingItem CHUNK_IO_THREAD = new IntegerSettingItem("chunk_io_service", "thread", 4);
}
