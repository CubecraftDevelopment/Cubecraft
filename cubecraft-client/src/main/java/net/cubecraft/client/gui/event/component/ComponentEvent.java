package net.cubecraft.client.gui.event.component;

import net.cubecraft.client.gui.event.GUIEvent;
import net.cubecraft.client.gui.node.Node;

public abstract class ComponentEvent<C extends Node> extends GUIEvent {
    protected final C component;

    public ComponentEvent(C component) {
        this.component = component;
    }

    public final C getComponent() {
        return component;
    }

    public final String getComponentID() {
        return this.component.getId();
    }
}
