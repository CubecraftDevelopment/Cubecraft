package ink.flybird.cubecraft.client.event;

import ink.flybird.cubecraft.client.CubecraftClient;
import ink.flybird.cubecraft.client.gui.screen.Screen;


public record ScreenInitializeEvent(CubecraftClient client, Screen screen) {}
