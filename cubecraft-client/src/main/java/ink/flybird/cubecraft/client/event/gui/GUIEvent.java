package ink.flybird.cubecraft.client.event.gui;

import ink.flybird.cubecraft.client.gui.GUIContext;

public abstract class GUIEvent {
    protected final GUIContext context;

    protected GUIEvent(GUIContext context) {
        this.context = context;
    }

    public GUIContext getContext() {
        return this.context;
    }
}
