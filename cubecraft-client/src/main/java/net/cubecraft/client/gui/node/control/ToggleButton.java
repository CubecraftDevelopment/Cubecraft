package net.cubecraft.client.gui.node.control;

import net.cubecraft.client.gui.base.Text;
import me.gb2022.commons.registry.TypeItem;
import org.w3c.dom.Element;

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
        return this.style + ":" + (this.enabled ? (this.selected || this.hovered ? "selected" : "normal" ): "disabled");
    }
}
