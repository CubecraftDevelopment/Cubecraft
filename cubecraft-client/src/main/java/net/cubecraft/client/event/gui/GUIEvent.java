package net.cubecraft.client.event.gui;

import net.cubecraft.client.gui.GUIContext;

public abstract class GUIEvent {
    protected final GUIContext context;

    protected GUIEvent(GUIContext context) {
        this.context = context;
    }

    public GUIContext getContext() {
        return this.context;
    }
}
