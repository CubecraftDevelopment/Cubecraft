package net.cubecraft.client.event.gui.component;

import net.cubecraft.client.context.ClientGUIContext;
import net.cubecraft.client.gui.screen.Screen;
import net.cubecraft.client.gui.node.Button;

public final class ButtonClickedEvent extends ComponentEvent {
    public ButtonClickedEvent(Button component, Screen parent, ClientGUIContext context) {
        super(component, parent, context);
    }

    public Button getButton() {
        return ((Button) this.component);
    }
}
