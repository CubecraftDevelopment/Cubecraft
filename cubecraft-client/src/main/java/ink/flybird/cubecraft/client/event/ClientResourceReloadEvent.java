package ink.flybird.cubecraft.client.event;

import ink.flybird.cubecraft.client.CubecraftClient;
import ink.flybird.cubecraft.client.resources.ResourceManager;


@Deprecated
public record ClientResourceReloadEvent(CubecraftClient client, ResourceManager resourceManager) {
}
