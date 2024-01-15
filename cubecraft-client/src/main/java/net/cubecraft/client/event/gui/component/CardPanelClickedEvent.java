package net.cubecraft.client.event.gui.component;

import net.cubecraft.client.context.ClientGUIContext;
import net.cubecraft.client.gui.node.CardPanel;
import net.cubecraft.client.gui.screen.Screen;

public final class CardPanelClickedEvent extends ComponentEvent {
    public CardPanelClickedEvent(CardPanel component, Screen parent, ClientGUIContext context) {
        super(component, parent, context);
    }

    public CardPanel getButton() {
        return ((CardPanel) this.component);
    }
}
