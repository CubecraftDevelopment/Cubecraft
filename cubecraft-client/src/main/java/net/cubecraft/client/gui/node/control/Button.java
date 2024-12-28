package net.cubecraft.client.gui.node.control;


import me.gb2022.commons.registry.TypeItem;
import me.gb2022.quantum3d.device.Mouse;
import me.gb2022.quantum3d.device.MouseButton;
import net.cubecraft.client.gui.event.component.ButtonClickedEvent;
import net.cubecraft.client.gui.node.Node;
import net.cubecraft.client.gui.node.ToggleGroup;
import org.w3c.dom.Element;

import java.util.function.Consumer;

@TypeItem("button")
public class Button extends Node implements ToggleGroup.Toggled {
    public boolean enabled = true;
    public boolean hovered = false;
    public boolean active = false;

    private Consumer<ButtonClickedEvent> actionListener = (b) -> this.context.getEventBus().callEvent(b);

    public void setActionListener(Consumer<ButtonClickedEvent> actionListener) {
        this.actionListener = actionListener;
    }

    @Override
    public void init(Element element) {
        super.init(element);
        if (!element.hasAttribute("enabled")) {
            return;
        }
        this.enabled = Boolean.parseBoolean(element.getAttribute("enabled"));
    }

    @Override
    public String getStatement() {
        return this.style + ":" + (this.enabled ? this.hovered ? "selected" : "normal" : "disabled");
    }

    @Override
    public void onMouseClicked(Mouse m, int fx, int fy, MouseButton button) {
        if (!isMouseInbound(fx, fy)) {
            super.onMouseClicked(m, fx, fy, button);
            return;
        }

        if (button != MouseButton.MOUSE_BUTTON_LEFT) {
            return;
        }

        this.actionListener.accept(new ButtonClickedEvent(this));
    }

    @Override
    public void onMousePosition(Mouse m, int fx, int fy) {
        this.hovered = this.isMouseInbound(fx, fy);
    }

    @Override
    public void setToggled(boolean toggled) {
        this.hovered = toggled;
    }
}
