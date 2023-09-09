package ink.flybird.cubecraft.client.internal.gui.event;

import ink.flybird.cubecraft.client.gui.GUIManager;
import ink.flybird.cubecraft.client.gui.event.ComponentEvent;
import ink.flybird.cubecraft.client.gui.screen.Screen;
import ink.flybird.cubecraft.client.internal.gui.component.Button;

public final class ButtonClickedEvent extends ComponentEvent {
    public ButtonClickedEvent(Button component, Screen parent, GUIManager context) {
        super(component, parent, context);
    }

    public Button getButton() {
        return ((Button) this.component);
    }
}
