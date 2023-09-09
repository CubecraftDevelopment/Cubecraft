package ink.flybird.cubecraft.client.internal.gui.event;

import ink.flybird.cubecraft.client.gui.GUIManager;
import ink.flybird.cubecraft.client.gui.event.ComponentEvent;
import ink.flybird.cubecraft.client.gui.screen.Screen;
import ink.flybird.cubecraft.client.internal.gui.component.CardPanel;

public final class CardPanelClickedEvent extends ComponentEvent {
    public CardPanelClickedEvent(CardPanel component, Screen parent, GUIManager context) {
        super(component, parent, context);
    }

    public CardPanel getButton() {
        return ((CardPanel) this.component);
    }
}
