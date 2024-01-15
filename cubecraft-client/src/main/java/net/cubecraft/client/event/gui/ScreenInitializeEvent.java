package net.cubecraft.client.event.gui;

import net.cubecraft.client.context.ClientGUIContext;
import net.cubecraft.client.event.gui.component.ComponentEvent;
import net.cubecraft.client.gui.screen.Screen;

public final class ScreenInitializeEvent extends ComponentEvent {
    public ScreenInitializeEvent(Screen screen, ClientGUIContext context) {
        super(screen, screen, context);
    }
}
