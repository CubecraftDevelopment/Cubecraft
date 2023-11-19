package net.cubecraft.client.internal.handler;

import net.cubecraft.client.CubecraftClient;
import net.cubecraft.client.gui.GUIContext;
import net.cubecraft.client.gui.ScreenUtil;
import net.cubecraft.client.gui.base.Popup;
import net.cubecraft.client.gui.base.Text;
import net.cubecraft.client.gui.font.FontAlignment;
import net.cubecraft.client.gui.node.Node;
import net.cubecraft.client.gui.node.Label;
import net.cubecraft.client.event.gui.component.ButtonClickedEvent;
import net.cubecraft.client.event.gui.component.ComponentInitializeEvent;
import net.cubecraft.client.gui.screen.HUDScreen;
import net.cubecraft.client.internal.gui.ScreenLocation;
import net.cubecraft.client.internal.gui.ScreenType;
import net.cubecraft.SharedContext;
import net.cubecraft.client.registry.ResourceRegistry;
import ink.flybird.fcommon.event.EventHandler;

import ink.flybird.fcommon.event.SubscribedEvent;

import java.util.Objects;


public class ScreenController {
    @EventHandler
    @SubscribedEvent("cubecraft:pause_screen")
    public void buttonClicked_PauseScreen(ButtonClickedEvent e) {
        if (Objects.equals(e.getButton().getId(), "button_resume")) {
            CubecraftClient.CLIENT.getGuiManager().setScreen(new HUDScreen());
        }
        if (Objects.equals(e.getButton().getId(), "button_option")) {

        }
        if (Objects.equals(e.getButton().getId(), "button_achievement")) {
            //todo:achievement screen
        }
        if (Objects.equals(e.getButton().getId(), "button_quit")) {
            CubecraftClient.CLIENT.getGuiManager().setScreen(ResourceRegistry.TITLE_SCREEN);
            CubecraftClient.CLIENT.getClientWorldManager().leaveWorld();
        }
    }

    @EventHandler
    @SubscribedEvent(ScreenType.TITLE)
    public void buttonClicked(ButtonClickedEvent event) {
        GUIContext context = event.getContext();
        switch (event.getComponentID()) {
            case "button_singleplayer" -> context.setScreen(ResourceRegistry.SINGLE_PLAYER_SCREEN,ResourceRegistry.TITLE_SCREEN);
            case "button_multiplayer" -> context.setScreen(ResourceRegistry.MULTI_PLAYER_SCREEN,ResourceRegistry.TITLE_SCREEN);
            case "button_option" -> context.setScreen(ScreenLocation.OPTION, ScreenLocation.TITLE);

            case "button_check_version" -> {
                ScreenUtil.createPopup(
                        SharedContext.I18N.get("version_check.start"),
                        SharedContext.I18N.get("version_check.start_detail"),
                        100, Popup.INFO
                );
            }
            case "button_account_setting" -> context.setScreen(ScreenLocation.ACCOUNT_SETTING, ScreenLocation.TITLE);

            case "button_quit" -> CubecraftClient.CLIENT.setRunning(false);
        }
    }

    @EventHandler
    public void onComponentInitialize(ComponentInitializeEvent event) {
        Text auth = Text.translated("title_screen.auth", FontAlignment.LEFT, CubecraftClient.CLIENT.getSession());
        Text version = Text.translated("title_screen.version", FontAlignment.LEFT,
                CubecraftClient.VERSION.shortVersion(), SharedContext.MOD.getLoadedMods().size());
        Node node = event.getComponent();
        try {
            switch (event.getComponentID()) {
                case "auth_str" -> ((Label) node).setText(auth);
                case "version_str" -> ((Label) node).setText(version);
            }
        }catch (Exception ignored){
        }
    }
}