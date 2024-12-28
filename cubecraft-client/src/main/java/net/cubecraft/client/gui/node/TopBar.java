package net.cubecraft.client.gui.node;

import me.gb2022.commons.event.EventHandler;
import me.gb2022.commons.registry.TypeItem;
import me.gb2022.quantum3d.device.MouseButton;
import me.gb2022.quantum3d.device.event.MouseClickEvent;
import me.gb2022.quantum3d.device.event.MousePosEvent;
import net.cubecraft.client.CubecraftClient;
import net.cubecraft.client.context.ClientGUIContext;
import net.cubecraft.client.gui.font.FontAlignment;
import net.cubecraft.client.gui.node.component.Component;
import net.cubecraft.text.TextComponent;

@TypeItem("topbar")
public final class TopBar extends Component {
    public static final TextComponent BACK_ICON = TextComponent.iconFont("F060");

    private boolean hovered;

    @EventHandler
    public void onClicked(MouseClickEvent e) {
        ClientGUIContext guiManager = CubecraftClient.getInstance().getClientGUIContext();
        if (hovered && e.getButton() == MouseButton.MOUSE_BUTTON_LEFT) {
            if (guiManager.getScreen().getParentScreen() != null) {
                guiManager.setScreen(guiManager.getScreen().getParentScreen());
            }
        }
    }

    @Override
    public String getStatement() {
        return this.style + ":" + (hovered ? "back_pressed" : "normal");
    }

    @EventHandler
    public void onMousePos(MousePosEvent e) {
        this.hovered = this.isMouseInbound();
    }

    @Override
    public TextComponent queryText(String query) {
        return BACK_ICON;
    }

    @Override
    public FontAlignment queryTextAlignment(String query) {
        return FontAlignment.LEFT;
    }

}
