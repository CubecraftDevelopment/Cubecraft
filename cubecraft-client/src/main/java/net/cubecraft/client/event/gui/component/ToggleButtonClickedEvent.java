package net.cubecraft.client.event.gui.component;

import net.cubecraft.client.gui.GUIContext;
import net.cubecraft.client.gui.node.ToggleButton;
import net.cubecraft.client.gui.screen.Screen;

public final class ToggleButtonClickedEvent extends ComponentEvent {
    private final boolean buttonSelected;

    public ToggleButtonClickedEvent(ToggleButton component, Screen parent, GUIContext context, boolean buttonSelected) {
        super(component, parent, context);
        this.buttonSelected = buttonSelected;
    }

    public ToggleButton getButton() {
        return ((ToggleButton) this.component);
    }

    public boolean isButtonSelected() {
        return buttonSelected;
    }
}
