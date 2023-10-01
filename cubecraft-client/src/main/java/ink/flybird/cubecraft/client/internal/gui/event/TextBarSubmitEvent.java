package ink.flybird.cubecraft.client.internal.gui.event;

import ink.flybird.cubecraft.client.gui.GUIManager;
import ink.flybird.cubecraft.client.event.gui.GUIEvent;
import ink.flybird.cubecraft.client.gui.node.Component;
import ink.flybird.cubecraft.client.gui.screen.Screen;
import ink.flybird.cubecraft.client.internal.gui.node.TextBar;

public final class TextBarSubmitEvent extends GUIEvent {
    private final String text;

    public TextBarSubmitEvent(Component component, Screen screen, GUIManager context, String text) {
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
