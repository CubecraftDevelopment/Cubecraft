package ink.flybird.cubecraft.client.gui.node;

import ink.flybird.cubecraft.client.CubecraftClient;
import ink.flybird.cubecraft.client.gui.GUIManager;
import ink.flybird.cubecraft.client.gui.base.Text;
import ink.flybird.cubecraft.client.gui.font.FontAlignment;
import ink.flybird.cubecraft.client.gui.node.Component;
import ink.flybird.fcommon.event.EventHandler;
import ink.flybird.quantum3d.device.MouseButton;
import ink.flybird.quantum3d.device.event.MouseClickEvent;
import ink.flybird.quantum3d.device.event.MousePosEvent;

public class TopBar extends Component {
    private boolean hovered;

    @EventHandler
    public void onClicked(MouseClickEvent e) {
        GUIManager guiManager = CubecraftClient.CLIENT.getGuiManager();
        if (hovered && e.getButton() == MouseButton.MOUSE_BUTTON_LEFT) {
            if (guiManager.getScreen().getParentScreen() != null) {
                guiManager.setScreen(guiManager.getScreen().getParentScreen());
            }
        }
    }

    @Override
    public String getStatement() {
        return hovered ? "back_pressed" : "default";
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
