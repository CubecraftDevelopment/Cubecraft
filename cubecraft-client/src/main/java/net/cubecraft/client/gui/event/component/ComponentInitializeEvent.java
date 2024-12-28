package net.cubecraft.client.gui.event.component;

import net.cubecraft.client.gui.node.Node;

public final class ComponentInitializeEvent extends ComponentEvent<Node> {
    public ComponentInitializeEvent(Node component) {
        super(component);
    }
}
