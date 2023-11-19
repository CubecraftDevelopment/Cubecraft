package net.cubecraft.client.registry;

import net.cubecraft.client.gui.layout.Layout;
import ink.flybird.fcommon.registry.ConstructingMap;
import ink.flybird.fcommon.registry.ItemRegisterFunc;
import net.cubecraft.client.gui.node.Node;

public class GUIRegistry {
    @ItemRegisterFunc(Node.class)
    public void registerNode(ConstructingMap<Node> nodes) {

    }

    @ItemRegisterFunc(Layout.class)
    public void registerLayout(ConstructingMap<Layout> layouts) {

    }
}
