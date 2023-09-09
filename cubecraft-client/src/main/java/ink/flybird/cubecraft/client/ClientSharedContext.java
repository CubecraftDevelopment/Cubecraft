package ink.flybird.cubecraft.client;

import ink.flybird.cubecraft.client.internal.renderer.font.SmoothedFontRenderer;
import ink.flybird.cubecraft.client.net.ClientNetHandler;
import ink.flybird.fcommon.query.SimpleQueryHandler;
import ink.flybird.fcommon.registry.ConstructingMap;
import io.flybird.cubecraft.register.EnvironmentPath;
import io.flybird.cubecraft.util.GameSetting;

public interface ClientSharedContext {
    SmoothedFontRenderer SMOOTH_FONT_RENDERER = new SmoothedFontRenderer();
    SmoothedFontRenderer ICON_FONT_RENDERER = new SmoothedFontRenderer();
    ConstructingMap<ClientNetHandler> NET_HANDLER = new ConstructingMap<>(ClientNetHandler.class);
    SimpleQueryHandler QUERY_HANDLER = new SimpleQueryHandler();


    KeyBindingController KEY_BINDING_CONTROLLER = new KeyBindingController(null,null);


    GameSetting KEY_BIND_SETTING = new GameSetting("/keybinding-setting.toml");
    GameSetting CLIENT_SETTING=new GameSetting("/client-setting.toml");
}
