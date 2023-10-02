package ink.flybird.cubecraft.client.registry;

import ink.flybird.cubecraft.client.gui.layout.Layout;
import ink.flybird.cubecraft.client.gui.node.*;
import ink.flybird.cubecraft.client.gui.layout.FlowLayout;
import ink.flybird.cubecraft.client.gui.layout.OriginLayout;
import ink.flybird.cubecraft.client.gui.layout.ViewportLayout;
import ink.flybird.fcommon.registry.ConstructingMap;
import ink.flybird.fcommon.registry.ItemRegisterFunc;

public class GUIRegistry {
    @ItemRegisterFunc(Node.class)
    public void registerNode(ConstructingMap<Node> nodes) {
        nodes.registerItem("scroll_panel", ScrollPanel.class);
        nodes.registerItem("panel", Panel.class);
        nodes.registerItem("button", Button.class);
        nodes.registerItem("image", ImageRenderer.class);
        nodes.registerItem("text", Label.class);
        nodes.registerItem("icon", Icon.class);
        nodes.registerItem("splash", SplashText.class);
        nodes.registerItem("topbar", TopBar.class);
        nodes.registerItem("textbar", TextBar.class);
    }

    @ItemRegisterFunc(Layout.class)
    public void registerLayout(ConstructingMap<Layout> layouts) {
        layouts.registerItem("origin", OriginLayout.class);
        layouts.registerItem("viewport", ViewportLayout.class);
        layouts.registerItem("flow", FlowLayout.class);
    }
}
