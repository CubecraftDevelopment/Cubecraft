package net.cubecraft.client.event.gui.component;

import net.cubecraft.client.context.ClientGUIContext;
import net.cubecraft.client.gui.node.Component;
import net.cubecraft.client.gui.node.TextBar;
import net.cubecraft.client.gui.screen.Screen;

public final class TextBarSubmitEvent extends ComponentEvent {
    private final String text;

    public TextBarSubmitEvent(Component component, Screen screen, ClientGUIContext context, String text) {
        super(component, screen, context);
        this.text = text;
    }

    public TextBar getTextBar() {
        return ((TextBar) this.component);
    }

    public String getText() {
        return text;
    }
}
