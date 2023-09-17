package ink.flybird.cubecraft.client.event.gui;

import ink.flybird.cubecraft.client.gui.node.Node;

public abstract class GUIEvent {
    private final Node node;

    protected GUIEvent(Node node) {
        this.node = node;
    }


}
