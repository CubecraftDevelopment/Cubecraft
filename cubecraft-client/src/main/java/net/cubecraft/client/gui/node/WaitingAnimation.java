package net.cubecraft.client.gui.node;

import net.cubecraft.client.gui.layout.Layout;
import me.gb2022.commons.file.FAMLDeserializer;
import me.gb2022.commons.file.XmlReader;
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
