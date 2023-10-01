package ink.flybird.cubecraft.client.internal.gui.event;

import ink.flybird.cubecraft.client.gui.GUIManager;
import ink.flybird.cubecraft.client.event.gui.GUIEvent;
import ink.flybird.cubecraft.client.gui.screen.Screen;
import ink.flybird.cubecraft.client.internal.gui.node.CardPanel;

public final class CardPanelClickedEvent extends GUIEvent {
    public CardPanelClickedEvent(CardPanel component, Screen parent, GUIManager context) {
        super(component, parent, context);
    }

    public CardPanel getButton() {
        return ((CardPanel) this.component);
    }
}
