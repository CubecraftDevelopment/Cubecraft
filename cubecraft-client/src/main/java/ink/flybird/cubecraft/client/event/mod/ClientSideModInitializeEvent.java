package ink.flybird.cubecraft.client.event.mod;

import ink.flybird.cubecraft.client.CubecraftClient;

public abstract class ClientSideModInitializeEvent {
    private final CubecraftClient client;

    protected ClientSideModInitializeEvent(CubecraftClient client) {
        this.client = client;
    }

    public CubecraftClient getClient() {
        return client;
    }
}
