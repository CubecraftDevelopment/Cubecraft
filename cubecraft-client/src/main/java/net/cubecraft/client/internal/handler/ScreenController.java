package net.cubecraft.client.internal.handler;

import me.gb2022.commons.event.EventHandler;
import me.gb2022.commons.event.SubscribedEvent;
import net.cubecraft.SharedContext;
import net.cubecraft.client.ClientSharedContext;
import net.cubecraft.client.CubecraftClient;
import net.cubecraft.client.context.ClientGUIContext;
import net.cubecraft.client.event.gui.component.ButtonClickedEvent;
import net.cubecraft.client.event.gui.component.ComponentInitializeEvent;
import net.cubecraft.client.gui.ScreenBuilders;
import net.cubecraft.client.gui.ScreenUtil;
import net.cubecraft.client.gui.base.Popup;
import net.cubecraft.client.gui.base.Text;
import net.cubecraft.client.gui.font.FontAlignment;
import net.cubecraft.client.gui.node.Label;
import net.cubecraft.client.gui.screen.HUDScreen;
import net.cubecraft.client.gui.screen.ScreenBuilder;
import net.cubecraft.client.internal.gui.ScreenType;
import net.cubecraft.client.registry.ResourceRegistry;

import java.util.Objects;


public class ScreenController {


    @EventHandler
    @SubscribedEvent("cubecraft:pause_screen")
    public void buttonClicked_PauseScreen(ButtonClickedEvent e) {
        if (Objects.equals(e.getButton().getId(), "button_resume")) {
            ClientSharedContext.getClient().getClientGUIContext().setScreen(new HUDScreen());
        }
        if (Objects.equals(e.getButton().getId(), "button_quit")) {
            ClientSharedContext.getClient().getClientGUIContext().setScreen(ScreenBuilders.TITLE_SCREEN);
            ClientSharedContext.getClient().getClientWorldManager().leaveWorld();
        }
    }

    @EventHandler
    @SubscribedEvent(ScreenType.TITLE)
    public void buttonClicked(ButtonClickedEvent event) {
        ClientGUIContext context = event.getContext();
        switch (event.getComponentID()) {
            case "button_singleplayer" ->
                    context.setScreen(ScreenBuilder.xml(ResourceRegistry.SINGLE_PLAYER_SCREEN, ResourceRegistry.TITLE_SCREEN));
            case "button_multiplayer" ->
                    context.setScreen(ScreenBuilder.xml(ResourceRegistry.MULTI_PLAYER_SCREEN, ResourceRegistry.TITLE_SCREEN));
            case "button_option" -> context.setScreen(ScreenBuilder.xml(ResourceRegistry.OPTIONS_SCREEN, ResourceRegistry.TITLE_SCREEN));

            case "button_check_version" -> ScreenUtil.createPopup(SharedContext.I18N.get("version_check.start"),
                                                                  SharedContext.I18N.get("version_check.start_detail"),
                                                                  100,
                                                                  Popup.INFO
            );
            case "button_quit" -> ClientSharedContext.getClient().setRunning(false);
        }
    }

    @EventHandler
    @SubscribedEvent("cubecraft:title_screen")
    public void onComponentInitialize(ComponentInitializeEvent event) {
        Text auth = Text.translated("title_screen.auth", FontAlignment.LEFT, ClientSharedContext.getClient().getSession());
        Text version = Text.translated("title_screen.version",
                                       FontAlignment.LEFT,
                                       CubecraftClient.VERSION.shortVersion(),
                                       SharedContext.MOD.getMods().size()
        );


        event.getComponent().getNodeOptional("auth", Label.class).ifPresent((c) -> c.setText(auth));
        event.getComponent().getNodeOptional("version", Label.class).ifPresent((c) -> c.setText(version));
    }
}