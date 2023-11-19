package net.cubecraft.client.event.gui.context;

import net.cubecraft.client.event.gui.GUIEvent;
import net.cubecraft.client.gui.GUIContext;

public abstract class GUIContextEvent extends GUIEvent {
    protected GUIContextEvent(GUIContext context) {
        super(context);
    }
}
