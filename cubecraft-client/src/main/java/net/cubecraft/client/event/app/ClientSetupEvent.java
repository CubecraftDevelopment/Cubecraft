package net.cubecraft.client.event.app;

import net.cubecraft.client.CubecraftClient;

public class ClientSetupEvent extends ClientEvent{
    public ClientSetupEvent(CubecraftClient client) {
        super(client);
    }
}
