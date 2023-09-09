package ink.flybird.cubecraft.client.event;

import ink.flybird.cubecraft.client.CubecraftClient;
import ink.flybird.cubecraft.client.render.LevelRenderer;


public record ClientRendererInitializeEvent(CubecraftClient client, LevelRenderer renderer) {
}
