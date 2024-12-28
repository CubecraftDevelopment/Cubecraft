package net.cubecraft.client.gui.event.component;

import net.cubecraft.client.gui.node.CardPanel;

public final class CardPanelClickedEvent extends ComponentEvent<CardPanel> {
    public CardPanelClickedEvent(CardPanel component) {
        super(component);
    }
}
