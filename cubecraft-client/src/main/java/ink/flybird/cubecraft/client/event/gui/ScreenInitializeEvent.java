package ink.flybird.cubecraft.client.event.gui;

import ink.flybird.cubecraft.client.event.gui.component.ComponentEvent;
import ink.flybird.cubecraft.client.gui.GUIContext;
import ink.flybird.cubecraft.client.gui.screen.Screen;

public final class ScreenInitializeEvent extends ComponentEvent {
    public ScreenInitializeEvent(Screen screen, GUIContext context) {
        super(screen, screen, context);
    }
}
