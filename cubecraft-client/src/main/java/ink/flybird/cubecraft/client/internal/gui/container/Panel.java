package ink.flybird.cubecraft.client.internal.gui.container;

import ink.flybird.cubecraft.client.gui.node.Container;
import ink.flybird.fcommon.file.FAMLDeserializer;
import ink.flybird.fcommon.file.XmlReader;
import org.w3c.dom.Element;

public class Panel extends Container {

    public static class XMLDeserializer implements FAMLDeserializer<Panel> {
        @Override
        public Panel deserialize(Element element, XmlReader reader) {
            Panel panel = new Panel();
            panel.deserializeLayout(element, reader);
            return panel;
        }
    }
}
