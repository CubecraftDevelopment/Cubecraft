package net.cubecraft.client.gui.node;

import net.cubecraft.client.event.gui.component.ToggleButtonClickedEvent;
import net.cubecraft.client.gui.base.Text;
import me.gb2022.commons.event.EventHandler;
import me.gb2022.commons.registry.TypeItem;
import me.gb2022.quantum3d.device.MouseButton;
import me.gb2022.quantum3d.device.event.MouseClickEvent;
import org.w3c.dom.Element;

import java.util.Objects;

@TypeItem("toggle_button")
public class ToggleButton extends Button {
    private Text text;
    public boolean selected;

    @Override
    public void init(Element element) {
        super.init(element);
        if (!element.hasAttribute("enabled")) {
            return;
        }
        this.enabled = Boolean.parseBoolean(element.getAttribute("enabled"));
    }

    public void setText(Text text) {
        this.text = text;
    }

    @Override
    public String getStatement() {
        return this.style + ":" + (this.enabled ? this.selected || this.hovered ? "selected" : "normal" : "disabled");
    }

    @EventHandler
    public void onClicked(MouseClickEvent e) {
        if (this.enabled && e.getButton() == MouseButton.MOUSE_BUTTON_LEFT) {
            this.selected = this.hovered;
            this.context.getEventBus().callEvent(new ToggleButtonClickedEvent(this, this.screen, this.context, this.selected), this.screen.getId());
        }
    }

    @Override
    public Text queryText(String query) {
        if (Objects.equals(query, "button:text")) {
            return this.text;
        }
        return super.queryText(query);
    }
}
