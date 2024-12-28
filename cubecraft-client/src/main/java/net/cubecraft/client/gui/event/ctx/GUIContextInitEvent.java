package net.cubecraft.client.gui.event.ctx;

import net.cubecraft.client.CubecraftClient;
import net.cubecraft.client.context.ClientGUIContext;
import net.cubecraft.client.event.ClientEvent;

public class GUIContextInitEvent extends ClientEvent {
    private final ClientGUIContext context;

    public GUIContextInitEvent(CubecraftClient client, ClientGUIContext context) {
        super(client);
        this.context=context;
    }

    public ClientGUIContext getContext() {
        return this.context;
    }
}
