package ink.flybird.cubecraft.client;

import ink.flybird.cubecraft.client.gui.font.SmoothedFontRenderer;
import ink.flybird.cubecraft.client.net.ClientNetHandler;
import ink.flybird.cubecraft.resource.ResourceManager;
import ink.flybird.fcommon.query.SimpleQueryHandler;
import ink.flybird.fcommon.registry.ConstructingMap;
import ink.flybird.cubecraft.util.setting.GameSetting;

public interface ClientSharedContext {
    SmoothedFontRenderer SMOOTH_FONT_RENDERER = new SmoothedFontRenderer();
    SmoothedFontRenderer ICON_FONT_RENDERER = new SmoothedFontRenderer();
    ConstructingMap<ClientNetHandler> NET_HANDLER = new ConstructingMap<>(ClientNetHandler.class);
    SimpleQueryHandler QUERY_HANDLER = new SimpleQueryHandler();


    KeyBindingController KEY_BINDING_CONTROLLER = new KeyBindingController(null,null);


    GameSetting CLIENT_CONTROL_SETTING = new GameSetting("/client-control-setting.toml");
    GameSetting CLIENT_SETTING=new GameSetting("/client-setting.toml");
    ResourceManager RESOURCE_MANAGER = new ResourceManager();
}
