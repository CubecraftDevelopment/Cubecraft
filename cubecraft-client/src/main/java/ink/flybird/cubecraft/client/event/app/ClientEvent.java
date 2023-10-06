package ink.flybird.cubecraft.client.event.app;

import ink.flybird.cubecraft.client.CubecraftClient;

public abstract class ClientEvent {
    private final CubecraftClient client;

    public ClientEvent(CubecraftClient client) {
        this.client = client;
    }

    public CubecraftClient getClient() {
        return client;
    }
}
