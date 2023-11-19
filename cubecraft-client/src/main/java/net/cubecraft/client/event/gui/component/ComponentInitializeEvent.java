package net.cubecraft.client.event.gui.component;

import net.cubecraft.client.gui.GUIContext;
import net.cubecraft.client.gui.node.Node;
import net.cubecraft.client.gui.screen.Screen;

public final class ComponentInitializeEvent extends ComponentEvent {
    public ComponentInitializeEvent(Node component, Screen screen, GUIContext context) {
        super(component, screen, context);
    }

    public Node getComponent() {
        return this.component;
    }
}
