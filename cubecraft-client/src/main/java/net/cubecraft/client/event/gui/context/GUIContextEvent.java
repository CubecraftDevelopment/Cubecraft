package net.cubecraft.client.event.gui.context;

import net.cubecraft.client.context.ClientGUIContext;
import net.cubecraft.client.event.gui.GUIEvent;

public abstract class GUIContextEvent extends GUIEvent {
    protected GUIContextEvent(ClientGUIContext context) {
        super(context);
    }
}
