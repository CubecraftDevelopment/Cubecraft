package net.cubecraft.client.event.gui;

import net.cubecraft.client.event.gui.component.ComponentEvent;
import net.cubecraft.client.gui.GUIContext;
import net.cubecraft.client.gui.screen.Screen;

public final class ScreenInitializeEvent extends ComponentEvent {
    public ScreenInitializeEvent(Screen screen, GUIContext context) {
        super(screen, screen, context);
    }
}
