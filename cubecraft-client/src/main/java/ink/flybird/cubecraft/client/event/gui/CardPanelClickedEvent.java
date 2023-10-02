package ink.flybird.cubecraft.client.event.gui;

import ink.flybird.cubecraft.client.gui.GUIManager;
import ink.flybird.cubecraft.client.gui.screen.Screen;
import ink.flybird.cubecraft.client.gui.node.CardPanel;

public final class CardPanelClickedEvent extends GUIEvent {
    public CardPanelClickedEvent(CardPanel component, Screen parent, GUIManager context) {
        super(component, parent, context);
    }

    public CardPanel getButton() {
        return ((CardPanel) this.component);
    }
}
