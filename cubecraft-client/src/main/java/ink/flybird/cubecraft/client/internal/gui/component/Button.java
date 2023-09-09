package ink.flybird.cubecraft.client.internal.gui.component;


import ink.flybird.cubecraft.client.gui.base.Text;
import ink.flybird.cubecraft.client.gui.node.Node;
import ink.flybird.cubecraft.client.internal.gui.event.ButtonClickedEvent;
import ink.flybird.quantum3d.device.MouseButton;
import ink.flybird.quantum3d.device.event.MouseClickEvent;
import ink.flybird.fcommon.event.EventHandler;
import ink.flybird.fcommon.file.FAMLDeserializer;
import ink.flybird.fcommon.file.XmlReader;
import org.w3c.dom.Element;

import java.util.Objects;

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

    @Override
    public void tick() {
        super.tick();
        int xm = this.context.getFixedMouseX();
        int ym = this.context.getFixedMouseY();
        int x0 = this.getLayout().getAbsoluteX();
        int x1 = x0 + this.getLayout().getAbsoluteWidth();
        int y0 = this.getLayout().getAbsoluteY();
        int y1 = y0 + this.getLayout().getAbsoluteHeight();
        this.hovered = xm > x0 && xm < x1 && ym > y0 && ym < y1;
    }

    @EventHandler
    public void onClicked(MouseClickEvent e) {
        if (hovered && enabled && e.getButton() == MouseButton.MOUSE_BUTTON_LEFT) {
            this.context.getEventBus().callEvent(new ButtonClickedEvent(this, this.screen, this.context), this.screen.getID());
        }
    }

    @Override
    public Text queryText(String query) {
        if (Objects.equals(query, "button:text")) {
            return this.text;
        }
        return super.queryText(query);
    }

    public static class XMLDeserializer implements FAMLDeserializer<Button> {
        public Button deserialize(Element element, XmlReader reader) {
            Button btn = new Button();
            btn.deserializeLayout(element, reader);
            return btn;
        }
    }
}
