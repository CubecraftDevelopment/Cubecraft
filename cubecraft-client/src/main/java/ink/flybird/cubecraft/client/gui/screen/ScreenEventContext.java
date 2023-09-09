package ink.flybird.cubecraft.client.gui.screen;

import ink.flybird.cubecraft.client.gui.GUIManager;

public record ScreenEventContext(
        GUIManager manager,
        Screen screen
) {
}
