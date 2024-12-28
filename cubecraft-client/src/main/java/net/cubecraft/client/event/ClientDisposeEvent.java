package net.cubecraft.client.event;

import net.cubecraft.client.CubecraftClient;

public class ClientDisposeEvent extends ClientEvent{
    public ClientDisposeEvent(CubecraftClient client) {
        super(client);
    }
}
