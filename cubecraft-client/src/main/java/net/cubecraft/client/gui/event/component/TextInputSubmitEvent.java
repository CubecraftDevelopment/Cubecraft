package net.cubecraft.client.gui.event.component;

import net.cubecraft.client.gui.node.control.TextInput;

public final class TextInputSubmitEvent extends ComponentEvent<TextInput> {
    private final String text;

    public TextInputSubmitEvent(TextInput component, String text) {
        super(component);
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
