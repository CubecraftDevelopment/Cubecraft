package net.cubecraft.client.gui.node;


import net.cubecraft.client.gui.base.Text;
import net.cubecraft.client.event.gui.component.ButtonClickedEvent;
import me.gb2022.commons.registry.TypeItem;
import me.gb2022.quantum3d.device.MouseButton;
import me.gb2022.quantum3d.device.event.MouseClickEvent;
import me.gb2022.commons.event.EventHandler;
import org.w3c.dom.Element;

import java.util.Objects;

@TypeItem("button")
public class Button extends Node {
    public boolean enabled = true;
    public boolean hovered = false;
    private Text text;

    @Override
    public void init(Element element) {
        super.init(element);
        if(!element.hasAttribute("enabled")){
            return;
        }
        this.enabled= Boolean.parseBoolean(element.getAttribute("enabled"));
    }

    public void setText(Text text) {
        this.text = text;
    }

    @Override
    public String getStatement() {
        return this.style + ":" + (this.enabled ? this.hovered ? "selected" : "normal" : "disabled");
    }

    @EventHandler
    public void onClicked(MouseClickEvent e) {
        if (this.hovered && this.enabled && e.getButton() == MouseButton.MOUSE_BUTTON_LEFT) {
            this.context.getEventBus().callEvent(new ButtonClickedEvent(this, this.screen, this.context), this.screen.getId());
        }
    }

    @Override
    public void tick() {
        this.hovered=this.isMouseInbound();
    }

    @Override
    public Text queryText(String query) {
        if (Objects.equals(query, "button:text")) {
            return this.text;
        }
        return super.queryText(query);
    }
}
