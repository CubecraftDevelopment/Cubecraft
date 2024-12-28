package net.cubecraft.client.internal.handler;

import me.gb2022.commons.event.EventHandler;
import me.gb2022.commons.event.SubscribedEvent;
import net.cubecraft.SharedContext;
import net.cubecraft.client.CubecraftClient;
import net.cubecraft.client.context.ClientGUIContext;
import net.cubecraft.client.gui.event.component.ButtonClickedEvent;
import net.cubecraft.client.gui.event.component.ComponentInitializeEvent;
import net.cubecraft.client.gui.ScreenBuilders;
import net.cubecraft.client.gui.node.component.Label;
import net.cubecraft.client.gui.screen.HUDScreen;
import net.cubecraft.client.gui.screen.ScreenBuilder;
import net.cubecraft.client.registry.ResourceRegistry;

import java.util.Objects;


public class ScreenController {
    @EventHandler
    @SubscribedEvent("cubecraft:pause_screen")
    public void buttonClicked_PauseScreen(ButtonClickedEvent e) {
        if (Objects.equals(e.getComponentID(), "button_resume")) {
            CubecraftClient.getInstance().getClientGUIContext().setScreen(new HUDScreen());
        }
        if (Objects.equals(e.getComponentID(), "button_quit")) {
            CubecraftClient.getInstance().getActiveLevelProvider().orElseThrow().leave();
            CubecraftClient.getInstance().getClientGUIContext().setScreen(ScreenBuilders.TITLE_SCREEN);
        }
    }

    @EventHandler
    @SubscribedEvent("cubecraft:title_screen")
    public void onComponentInitialize(ComponentInitializeEvent event) {
        event.getComponent()
                .getNodeOptional("auth", Label.class)
                .ifPresent((c) -> c.getText().translatable().setFormat(CubecraftClient.getInstance().getSession()));

        event.getComponent()
                .getNodeOptional("version", Label.class)
                .ifPresent((c) -> c.getText()
                        .translatable()
                        .setFormat(CubecraftClient.VERSION.shortVersion(), SharedContext.MOD.getMods().size()));
    }
}