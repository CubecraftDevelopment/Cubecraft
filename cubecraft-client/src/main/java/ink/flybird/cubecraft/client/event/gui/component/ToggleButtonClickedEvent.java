package ink.flybird.cubecraft.client.event.gui.component;

import ink.flybird.cubecraft.client.gui.GUIContext;
import ink.flybird.cubecraft.client.gui.node.ToggleButton;
import ink.flybird.cubecraft.client.gui.screen.Screen;

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
