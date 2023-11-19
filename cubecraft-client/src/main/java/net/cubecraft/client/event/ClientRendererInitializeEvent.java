package net.cubecraft.client.event;

import net.cubecraft.client.CubecraftClient;
import net.cubecraft.client.render.LevelRenderer;


public record ClientRendererInitializeEvent(CubecraftClient client, LevelRenderer renderer) {
}
