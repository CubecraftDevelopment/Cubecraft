package net.cubecraft.client.event;

import net.cubecraft.client.CubecraftClient;
import net.cubecraft.client.context.ClientRenderContext;
import net.cubecraft.client.event.app.ClientEvent;

public class ClientRenderContextInitEvent extends ClientEvent {
    private final ClientRenderContext context;

    public ClientRenderContextInitEvent(CubecraftClient client, ClientRenderContext context) {
        super(client);
        this.context = context;
    }

    public ClientRenderContext getContext() {
        return context;
    }
}
