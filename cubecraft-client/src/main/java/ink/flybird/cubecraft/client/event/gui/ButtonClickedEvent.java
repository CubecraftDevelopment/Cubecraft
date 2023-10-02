package ink.flybird.cubecraft.client.event.gui;

import ink.flybird.cubecraft.client.gui.GUIManager;
import ink.flybird.cubecraft.client.gui.screen.Screen;
import ink.flybird.cubecraft.client.gui.node.Button;

public final class ButtonClickedEvent extends GUIEvent {
    public ButtonClickedEvent(Button component, Screen parent, GUIManager context) {
        super(component, parent, context);
    }

    public Button getButton() {
        return ((Button) this.component);
    }
}
