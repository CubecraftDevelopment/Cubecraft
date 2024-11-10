package net.cubecraft.client.gui.node;

import me.gb2022.commons.event.EventHandler;
import me.gb2022.commons.registry.TypeItem;
import me.gb2022.quantum3d.device.MouseButton;
import me.gb2022.quantum3d.device.event.MouseClickEvent;
import me.gb2022.quantum3d.device.event.MousePosEvent;
import net.cubecraft.client.ClientSharedContext;
import net.cubecraft.client.context.ClientGUIContext;
import net.cubecraft.client.gui.base.Text;
import net.cubecraft.client.gui.font.FontAlignment;

@TypeItem("topbar")
public class TopBar extends Component {
    private boolean hovered;

    @EventHandler
    public void onClicked(MouseClickEvent e) {
        ClientGUIContext guiManager = ClientSharedContext.getClient().getClientGUIContext();
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
    public Text queryText(String query) {
        return new Text("  \ue72b ", 0xffffff, FontAlignment.LEFT, true);
    }

}
