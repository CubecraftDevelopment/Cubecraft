package ink.flybird.cubecraft.client.event.gui.component;

import ink.flybird.cubecraft.client.gui.GUIContext;
import ink.flybird.cubecraft.client.gui.screen.Screen;
import ink.flybird.cubecraft.client.gui.node.CardPanel;

public final class CardPanelClickedEvent extends ComponentEvent {
    public CardPanelClickedEvent(CardPanel component, Screen parent, GUIContext context) {
        super(component, parent, context);
    }

    public CardPanel getButton() {
        return ((CardPanel) this.component);
    }
}
