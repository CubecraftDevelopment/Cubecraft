package ink.flybird.cubecraft.client.internal.gui.node;

import ink.flybird.cubecraft.client.gui.layout.Layout;
import ink.flybird.cubecraft.client.gui.node.Component;
import ink.flybird.fcommon.file.FAMLDeserializer;
import ink.flybird.fcommon.file.XmlReader;
import org.w3c.dom.Element;

public class WaitingAnimation extends Component {
    public static class XMLDeserializer implements FAMLDeserializer<WaitingAnimation> {
        @Override
        public WaitingAnimation deserialize(Element element, XmlReader reader) {
            WaitingAnimation ani=new WaitingAnimation();
            ani.setLayout(reader.deserialize((Element) element.getElementsByTagName("layout").item(0), Layout.class));
            return ani;
        }
    }
}
