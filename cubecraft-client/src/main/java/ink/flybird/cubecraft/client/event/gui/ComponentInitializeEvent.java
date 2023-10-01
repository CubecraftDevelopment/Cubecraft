package ink.flybird.cubecraft.client.event.gui;

import ink.flybird.cubecraft.client.gui.GUIManager;
import ink.flybird.cubecraft.client.gui.node.Node;
import ink.flybird.cubecraft.client.gui.screen.Screen;

public final class ComponentInitializeEvent extends GUIEvent {
    public ComponentInitializeEvent(Node component, Screen screen, GUIManager context) {
        super(component, screen, context);
    }

    public Node getComponent() {
        return this.component;
    }
}
