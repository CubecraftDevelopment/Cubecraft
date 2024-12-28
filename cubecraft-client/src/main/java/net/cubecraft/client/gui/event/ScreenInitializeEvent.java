package net.cubecraft.client.gui.event;

import net.cubecraft.client.gui.event.component.ComponentEvent;
import net.cubecraft.client.gui.screen.Screen;

public final class ScreenInitializeEvent extends ComponentEvent<Screen> {
    public ScreenInitializeEvent(Screen screen) {
        super(screen);
    }
}
