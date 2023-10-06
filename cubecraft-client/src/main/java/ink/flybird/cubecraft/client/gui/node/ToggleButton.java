package ink.flybird.cubecraft.client.gui.node;

import ink.flybird.cubecraft.client.event.gui.component.ToggleButtonClickedEvent;
import ink.flybird.cubecraft.client.gui.base.Text;
import ink.flybird.fcommon.event.EventHandler;
import ink.flybird.fcommon.registry.TypeItem;
import ink.flybird.quantum3d.device.MouseButton;
import ink.flybird.quantum3d.device.event.MouseClickEvent;
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
