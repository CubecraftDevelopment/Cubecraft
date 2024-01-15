package net.cubecraft.client;

import ink.flybird.fcommon.query.SimpleQueryHandler;
import ink.flybird.fcommon.registry.ConstructingMap;
import net.cubecraft.client.net.ClientNetHandler;
import net.cubecraft.resource.ResourceManager;
import net.cubecraft.util.ObjectContainer;
import net.cubecraft.util.setting.GameSetting;

public interface ClientSharedContext {
    ConstructingMap<ClientNetHandler> NET_HANDLER = new ConstructingMap<>(ClientNetHandler.class);
    SimpleQueryHandler QUERY_HANDLER = new SimpleQueryHandler();

    GameSetting CLIENT_CONTROL_SETTING = new GameSetting("/client-control-setting.toml");
    GameSetting CLIENT_SETTING = new GameSetting("/client-setting.toml");
    ResourceManager RESOURCE_MANAGER = new ResourceManager();


    ObjectContainer<CubecraftClient> CLIENT_INSTANCE = new ObjectContainer<>(null);

    static CubecraftClient getClient() {
        return CLIENT_INSTANCE.getObj();
    }
}
