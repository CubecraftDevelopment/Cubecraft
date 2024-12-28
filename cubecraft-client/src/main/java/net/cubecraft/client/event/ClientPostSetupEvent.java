package net.cubecraft.client.event;

import net.cubecraft.client.CubecraftClient;

public class ClientPostSetupEvent extends ClientEvent{
    public ClientPostSetupEvent(CubecraftClient client) {
        super(client);
    }
}
