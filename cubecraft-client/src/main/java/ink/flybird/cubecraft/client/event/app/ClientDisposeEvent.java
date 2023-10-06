package ink.flybird.cubecraft.client.event.app;

import ink.flybird.cubecraft.client.CubecraftClient;

public class ClientDisposeEvent extends ClientEvent{
    public ClientDisposeEvent(CubecraftClient client) {
        super(client);
    }
}
