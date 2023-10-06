package ink.flybird.cubecraft.client.event.gui.component;

import ink.flybird.cubecraft.client.gui.GUIContext;
import ink.flybird.cubecraft.client.gui.screen.Screen;
import ink.flybird.cubecraft.client.gui.node.Button;

public final class ButtonClickedEvent extends ComponentEvent {
    public ButtonClickedEvent(Button component, Screen parent, GUIContext context) {
        super(component, parent, context);
    }

    public Button getButton() {
        return ((Button) this.component);
    }
}
