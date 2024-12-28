package net.cubecraft.client.internal.plugins;

import me.gb2022.commons.event.EventHandler;
import me.gb2022.quantum3d.device.DeviceContext;
import me.gb2022.quantum3d.device.Window;
import net.cubecraft.client.ClientComponent;
import net.cubecraft.client.CubecraftClient;
import net.cubecraft.client.gui.UI;
import net.cubecraft.client.gui.event.component.ButtonClickedEvent;
import net.cubecraft.client.gui.event.component.ComponentInitializeEvent;
import net.cubecraft.client.gui.screen.HUDScreen;
import net.cubecraft.client.gui.screen.Screen;
import net.cubecraft.client.gui.screen.ScreenBuilder;
import net.cubecraft.client.registry.ResourceRegistry;

import java.util.HashMap;
import java.util.Map;

public final class ScreenControllerBinder extends ClientComponent {
    private final Map<String, Object> controllers = new HashMap<>();

    @Override
    public void deviceSetup(CubecraftClient client, Window window, DeviceContext ctx) {
        client.getClientEventBus().registerEventListener(this);

        registerController("cubecraft:title_screen", new TitleScreen());
        registerController("cubecraft:pause_screen", new PauseScreen());
    }

    @EventHandler
    public void onScreenInit(ComponentInitializeEvent event) {
        if (!(event.getComponent() instanceof Screen screen)) {
            return;
        }

        var controller = this.controllers.get(event.getComponentID());

        if (controller == null) {
            return;
        }

        UI.Binder.bind(controller, screen);
    }

    public void registerController(String key, Object c) {
        this.controllers.put(key, c);
    }


    private final class TitleScreen implements UI.Controller {
        @UI
        void singlePlayer(ButtonClickedEvent event) {
            setScreen(ScreenBuilder.xml(ResourceRegistry.SINGLE_PLAYER_SCREEN, ResourceRegistry.TITLE_SCREEN));
        }

        @UI
        void options(ButtonClickedEvent event) {
            setScreen(ScreenBuilder.xml(ResourceRegistry.OPTIONS_SCREEN, ResourceRegistry.TITLE_SCREEN));
        }

        @UI
        void quit(ButtonClickedEvent event) {
            getClient().setRunning(false);
        }
    }

    private final class PauseScreen implements UI.Controller {
        @UI
        void back(ButtonClickedEvent event) {
            setScreen(ScreenBuilder.object(HUDScreen.class));
        }

        @UI
        void quit(ButtonClickedEvent event) {
            //todo:waiting animation
            setScreen(ScreenBuilder.xml(ResourceRegistry.TITLE_SCREEN));
            getClient().getActiveLevelProvider().orElseThrow().leave();
        }

        @UI
        void options(ButtonClickedEvent event) {
            setScreen(ScreenBuilder.xml(ResourceRegistry.OPTIONS_SCREEN, ResourceRegistry.PAUSE_SCREEN));
        }
    }
}
