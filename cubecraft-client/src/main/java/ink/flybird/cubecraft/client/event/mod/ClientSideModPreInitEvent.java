package ink.flybird.cubecraft.client.event.mod;

import ink.flybird.cubecraft.client.CubecraftClient;

public class ClientSideModPreInitEvent extends ClientSideModInitializeEvent{
    protected ClientSideModPreInitEvent(CubecraftClient client) {
        super(client);
    }
}
