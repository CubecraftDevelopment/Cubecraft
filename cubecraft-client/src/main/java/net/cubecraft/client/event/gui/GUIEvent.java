package net.cubecraft.client.event.gui;

import net.cubecraft.client.context.ClientGUIContext;

public abstract class GUIEvent {
    protected final ClientGUIContext context;

    protected GUIEvent(ClientGUIContext context) {
        this.context = context;
    }

    public ClientGUIContext getContext() {
        return this.context;
    }
}
