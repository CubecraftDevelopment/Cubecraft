package ink.flybird.cubecraft.client.event.gui;

import ink.flybird.cubecraft.client.gui.GUIManager;
import ink.flybird.cubecraft.client.gui.screen.Screen;

public final class ScreenInitializeEvent extends GUIEvent {
    public ScreenInitializeEvent(Screen screen, GUIManager context) {
        super(screen, screen, context);
    }
}
