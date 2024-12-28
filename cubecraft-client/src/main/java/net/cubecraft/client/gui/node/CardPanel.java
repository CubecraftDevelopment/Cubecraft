package net.cubecraft.client.gui.node;

import net.cubecraft.client.context.ClientGUIContext;
import net.cubecraft.client.gui.event.component.CardPanelClickedEvent;
import me.gb2022.commons.event.EventHandler;
import me.gb2022.commons.file.FAMLDeserializer;
import me.gb2022.commons.file.XmlReader;
import me.gb2022.commons.registry.TypeItem;
import me.gb2022.quantum3d.device.MouseButton;
import me.gb2022.quantum3d.device.event.MouseClickEvent;
import me.gb2022.quantum3d.device.event.MousePosEvent;
import org.w3c.dom.Element;

@TypeItem("card_panel")
public class CardPanel extends Container {
    public boolean enabled = true;
    public boolean hovered = false;

    @Override
    public void render(float interpolationTime) {
        ClientGUIContext.COMPONENT_RENDERER.get(this.getClass()).render(this);
        super.render(interpolationTime);
    }

    @Override
    public String getStatement() {
        return this.style + ":" + (this.enabled ? this.hovered ? "selected" : "normal" : "disabled");
    }

    @EventHandler
    public void onClicked(MouseClickEvent e) {
        if (this.hovered && this.enabled && e.getButton() == MouseButton.MOUSE_BUTTON_LEFT) {
            this.context.getEventBus().callEvent(new CardPanelClickedEvent(this), this.screen.getId());
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
