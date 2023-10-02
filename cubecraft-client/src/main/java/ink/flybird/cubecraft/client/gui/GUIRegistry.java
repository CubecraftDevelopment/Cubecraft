package ink.flybird.cubecraft.client.gui;

import ink.flybird.cubecraft.client.gui.layout.Border;
import ink.flybird.cubecraft.client.gui.layout.Layout;
import ink.flybird.cubecraft.client.gui.node.Node;
import ink.flybird.fcommon.registry.ConstructingMap;

import org.w3c.dom.Element;

import java.util.Objects;

public interface GUIRegistry {
    ConstructingMap<Node> NODE = new ConstructingMap<>(Node.class);
    ConstructingMap<Layout> LAYOUT = new ConstructingMap<>(Layout.class);


    static Layout createLayout(String content, String border) {
        String type = content.split("/")[0];
        String cont = content.split("/")[1];

        Layout layout = LAYOUT.create(type);

        layout.initialize(cont.split(","));

        if (Objects.equals(border, "")) {
            return layout;
        }

        layout.setBorder(new Border(
                Integer.parseInt(border.split(",")[0]),
                Integer.parseInt(border.split(",")[1]),
                Integer.parseInt(border.split(",")[3]),
                Integer.parseInt(border.split(",")[2])
        ));
        return layout;
    }

    static Node createNode(String type, Element element) {
        Node n = NODE.create(type);
        n.init(element);

        return n;
    }

}
