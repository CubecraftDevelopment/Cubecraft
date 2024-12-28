package net.cubecraft.client.event;

import net.cubecraft.client.CubecraftClient;

public abstract class ClientEvent {
    private final CubecraftClient client;

    public ClientEvent(CubecraftClient client) {
        this.client = client;
    }

    public CubecraftClient getClient() {
        return client;
    }
}
