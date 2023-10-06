package ink.flybird.cubecraft.client.event.gui.component;

import ink.flybird.cubecraft.client.gui.GUIContext;
import ink.flybird.cubecraft.client.gui.node.Node;
import ink.flybird.cubecraft.client.gui.screen.Screen;

public final class ComponentInitializeEvent extends ComponentEvent {
    public ComponentInitializeEvent(Node component, Screen screen, GUIContext context) {
        super(component, screen, context);
    }

    public Node getComponent() {
        return this.component;
    }
}
