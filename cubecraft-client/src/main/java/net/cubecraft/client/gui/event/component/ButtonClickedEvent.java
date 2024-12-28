package net.cubecraft.client.gui.event.component;

import net.cubecraft.client.gui.node.control.Button;

public final class ButtonClickedEvent extends ComponentEvent<Button> {
    public ButtonClickedEvent(Button component) {
        super(component);
    }
}
