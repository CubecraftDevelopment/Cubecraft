package net.cubecraft.client.event.gui.component;

import net.cubecraft.client.gui.GUIContext;
import net.cubecraft.client.gui.screen.Screen;
import net.cubecraft.client.gui.node.CardPanel;

public final class CardPanelClickedEvent extends ComponentEvent {
    public CardPanelClickedEvent(CardPanel component, Screen parent, GUIContext context) {
        super(component, parent, context);
    }

    public CardPanel getButton() {
        return ((CardPanel) this.component);
    }
}
