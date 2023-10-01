package ink.flybird.cubecraft.client.internal.handler;

import ink.flybird.cubecraft.client.CubecraftClient;
import ink.flybird.cubecraft.client.VersionCheck;
import ink.flybird.cubecraft.client.gui.GUIManager;
import ink.flybird.cubecraft.client.gui.ScreenUtil;
import ink.flybird.cubecraft.client.gui.base.Popup;
import ink.flybird.cubecraft.client.gui.base.Text;
import ink.flybird.cubecraft.client.gui.font.FontAlignment;
import ink.flybird.cubecraft.client.gui.node.Node;
import ink.flybird.cubecraft.client.internal.gui.node.Label;
import ink.flybird.cubecraft.client.internal.gui.event.ButtonClickedEvent;
import ink.flybird.cubecraft.client.event.gui.ComponentInitializeEvent;
import ink.flybird.cubecraft.client.internal.gui.screen.HUDScreen;
import ink.flybird.cubecraft.client.internal.gui.screen.ScreenLocation;
import ink.flybird.cubecraft.client.internal.gui.screen.ScreenType;
import ink.flybird.cubecraft.SharedContext;
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
            CubecraftClient.CLIENT.getGuiManager().setScreen("cubecraft:title_screen.xml");
            CubecraftClient.CLIENT.leaveWorld();
        }
    }

    @EventHandler
    @SubscribedEvent(ScreenType.TITLE)
    public void buttonClicked(ButtonClickedEvent event) {
        GUIManager context = event.getContext();
        switch (event.getComponentID()) {
            case "button_singleplayer" -> context.setScreen(ScreenLocation.SINGLE_PLAYER, ScreenLocation.TITLE);
            case "button_multiplayer" -> context.setScreen(ScreenLocation.MULTI_PLAYER, ScreenLocation.TITLE);
            case "button_option" -> context.setScreen(ScreenLocation.OPTION, ScreenLocation.TITLE);

            case "button_check_version" -> {
                ScreenUtil.createPopup(
                        SharedContext.I18N.get("version_check.start"),
                        SharedContext.I18N.get("version_check.start_detail"),
                        100, Popup.INFO
                );
                VersionCheck.check();
            }
            case "button_account_setting" -> context.setScreen(ScreenLocation.ACCOUNT_SETTING, ScreenLocation.TITLE);

            case "button_quit" -> CubecraftClient.CLIENT.setRunning(false);
        }
    }

    @EventHandler
    @SubscribedEvent(ScreenType.TITLE)
    public void onComponentInitialize(ComponentInitializeEvent event) {
        Text auth = Text.translated("title_screen.auth", FontAlignment.LEFT, "NULL");
        Text version = Text.translated("title_screen.version", FontAlignment.LEFT,
                CubecraftClient.VERSION, SharedContext.MOD.getLoadedMods().size());

        Node node = event.getComponent();
        switch (event.getComponentID()) {
            case "auth_str" -> ((Label) node).setText(auth);
            case "version_str" -> ((Label) node).setText(version);
        }
    }
}