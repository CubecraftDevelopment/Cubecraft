package ink.flybird.cubecraft.client.internal.gui.component;

import ink.flybird.cubecraft.client.gui.node.Container;
import ink.flybird.cubecraft.client.internal.gui.event.CardPanelClickedEvent;
import ink.flybird.fcommon.event.EventHandler;
import ink.flybird.fcommon.file.FAMLDeserializer;
import ink.flybird.fcommon.file.XmlReader;
import ink.flybird.quantum3d.device.MouseButton;
import ink.flybird.quantum3d.device.event.MouseClickEvent;
import org.w3c.dom.Element;

public class CardPanel extends Container {
    public boolean enabled = true;
    public boolean hovered = false;

    @Override
    public void render(float interpolationTime) {
        this.context.getRenderController(this.getClass()).render(this);
        super.render(interpolationTime);
    }

    @Override
    public String getStatement() {
        return this.style + ":" + (this.enabled ? this.hovered ? "selected" : "normal" : "disabled");
    }

    @Override
    public void tick() {
        super.tick();
        int xm=this.context.getFixedMouseX();
        int ym=this.context.getFixedMouseY();
        int x0 = this.getLayout().getAbsoluteX();
        int x1 = x0 + this.getLayout().getAbsoluteWidth();
        int y0 = this.getLayout().getAbsoluteY();
        int y1 = y0 + this.getLayout().getAbsoluteHeight();
        this.hovered = xm > x0 && xm < x1 && ym > y0 && ym < y1;
    }

    @EventHandler
    public void onClicked(MouseClickEvent e) {
        if (hovered && enabled && e.getButton() == MouseButton.MOUSE_BUTTON_LEFT) {
            this.context.getEventBus().callEvent(new CardPanelClickedEvent(this, this.screen, this.context), this.screen.getID());
        }
    }

    public static class XMLDeserializer implements FAMLDeserializer<CardPanel> {
        @Override
        public CardPanel deserialize(Element element, XmlReader reader) {
            CardPanel panel = new CardPanel(
            );
            panel.deserializeLayout(element, reader);
            return panel;
        }
    }
}
