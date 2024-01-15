package net.cubecraft.client.internal.gui;

import ink.flybird.fcommon.event.EventHandler;
import net.cubecraft.client.ClientSharedContext;
import net.cubecraft.client.CubecraftClient;
import net.cubecraft.client.event.gui.component.ButtonClickedEvent;
import net.cubecraft.client.gui.controller.ScreenController;

public class TitleScreenController extends ScreenController {
    private final CubecraftClient client = ClientSharedContext.getClient();

    @Override
    public void initializeController() {
        this.client.getClientDeviceContext().attachListener(this);
    }

    @EventHandler
    public void onQuitButtonClicked(ButtonClickedEvent event) {
        this.client.stop();
    }


}
