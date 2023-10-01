package ink.flybird.cubecraft.client.internal.gui.node;

import ink.flybird.cubecraft.client.gui.node.Container;
import ink.flybird.cubecraft.client.internal.gui.event.CardPanelClickedEvent;
import ink.flybird.fcommon.event.EventHandler;
import ink.flybird.fcommon.file.FAMLDeserializer;
import ink.flybird.fcommon.file.XmlReader;
import ink.flybird.quantum3d.device.MouseButton;
import ink.flybird.quantum3d.device.event.MouseClickEvent;
import ink.flybird.quantum3d.device.event.MousePosEvent;
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

    @EventHandler
    public void onClicked(MouseClickEvent e) {
        if (this.hovered && this.enabled && e.getButton() == MouseButton.MOUSE_BUTTON_LEFT) {
            this.context.getEventBus().callEvent(new CardPanelClickedEvent(this, this.screen, this.context), this.screen.getID());
        }
    }

    @EventHandler
    public void onMousePos(MousePosEvent event) {
        this.hovered = this.isMouseInbound();
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
