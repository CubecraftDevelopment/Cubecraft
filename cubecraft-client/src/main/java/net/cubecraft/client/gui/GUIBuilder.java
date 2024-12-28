package net.cubecraft.client.gui;

import net.cubecraft.client.context.ClientGUIContext;
import net.cubecraft.client.gui.layout.Border;
import net.cubecraft.client.gui.layout.Layout;
import net.cubecraft.client.gui.node.Node;
import org.w3c.dom.Element;

import java.util.Objects;

public interface GUIBuilder {
    static Layout createLayout(String content, String border) {
        var id = content.split("\\(")[0];
        var args = content.split("\\(")[1].replace(")", "").split(",");

        Layout layout = ClientGUIContext.LAYOUT.create(id);

        layout.initialize(args);

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
        Node n = ClientGUIContext.NODE.create(type);
        n.init(element);

        return n;
    }
}
