package ink.flybird.cubecraft.client.event.gui.component;

import ink.flybird.cubecraft.client.gui.GUIContext;
import ink.flybird.cubecraft.client.gui.node.Component;
import ink.flybird.cubecraft.client.gui.screen.Screen;
import ink.flybird.cubecraft.client.gui.node.TextBar;

public final class TextBarSubmitEvent extends ComponentEvent {
    private final String text;

    public TextBarSubmitEvent(Component component, Screen screen, GUIContext context, String text) {
        super(component, screen, context);
        this.text = text;
    }

    public TextBar getTextBar(){
        return ((TextBar) this.component);
    }

    public String getText() {
        return text;
    }
}
