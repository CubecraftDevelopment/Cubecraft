package net.cubecraft.client.gui.node;

import net.cubecraft.client.context.ClientGUIContext;
import net.cubecraft.client.event.gui.component.ComponentInitializeEvent;
import net.cubecraft.client.gui.ComponentRenderer;
import net.cubecraft.client.gui.GUIBuilder;
import net.cubecraft.client.gui.base.Text;
import net.cubecraft.client.gui.font.FontAlignment;
import net.cubecraft.client.gui.layout.Layout;
import net.cubecraft.client.gui.layout.Scale;
import net.cubecraft.client.gui.screen.Screen;
import me.gb2022.commons.container.MultiMap;
import me.gb2022.commons.file.DocumentUtil;
import me.gb2022.commons.file.XmlReader;
import ink.flybird.quantum3d_legacy.GLUtil;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public abstract class Node {
    protected final MultiMap<String, Node> nodes = new MultiMap<>();
    protected Element element;

    protected String style;
    protected Layout layout;
    protected String id;
    protected Node parent;
    protected Screen screen;
    protected ClientGUIContext context;

    public void init(Element element) {
        if (element == null) {
            throw new IllegalArgumentException("null element for deserializing node!");
        }
        this.element = element;
        this.id = DocumentUtil.getAttribute(element, "id", String.valueOf(element.hashCode()));
        this.style = DocumentUtil.getAttribute(element, "style", "default");
        this.layout = GUIBuilder.createLayout(element.getAttribute("layout"), element.getAttribute("border"));

        if (element.hasAttribute("scale")) {
            this.layout.setScale(new Scale(
                    Boolean.parseBoolean(this.element.getAttribute("scale").split(",")[0]),
                    Boolean.parseBoolean(this.element.getAttribute("scale").split(",")[1]),
                    Boolean.parseBoolean(this.element.getAttribute("scale").split(",")[3]),
                    Boolean.parseBoolean(this.element.getAttribute("scale").split(",")[2])
            ));
        }

        this.deserializeChild(element);
    }

    public void setContext(Screen screen, Node parent, ClientGUIContext context) {
        this.screen = screen;
        this.parent = parent;
        this.context = context;
        this.context.getDeviceEventBus().registerEventListener(this);
        for (Node n : nodes.values()) {
            n.setContext(screen, parent, context);
        }
        this.context.getEventBus().callEvent(new ComponentInitializeEvent(this, this.screen, this.context), this.getId());
    }

    //context
    public Node getParent() {
        return parent;
    }

    public void setParent(Container parent) {
        this.parent = parent;
    }

    public Screen getScreen() {
        return screen;
    }

    public ClientGUIContext getContext() {
        return context;
    }

    public String getId() {
        return this.id;
    }

    public Layout getLayout() {
        return this.layout;
    }

    public void setLayout(Layout layout) {
        this.layout = layout;
    }

    //query
    public String getStatement() {
        return "default";
    }

    public Text queryText(String query) {
        return new Text("", 0, FontAlignment.MIDDLE);
    }

    public double queryNum(String query) {
        return 0;
    }

    public void deserializeLayout(Element element, XmlReader reader) {
        this.setLayout(reader.deserialize((Element) element.getElementsByTagName("layout").item(0), Layout.class));
    }


    public void onResize(int x, int y, int w, int h) {
        if (this.layout != null) {
            this.layout.resize(x, y, w, h);
            for (Node node : this.nodes.values()) {
                node.onResize(this.layout.getAbsoluteX(), this.layout.getAbsoluteY(), this.layout.getAbsoluteWidth(), this.layout.getAbsoluteHeight());
            }
        } else {
            for (Node node : this.nodes.values()) {
                node.onResize(x, y, w, h);
            }
        }
    }

    public void destroy() {
        this.context.getDeviceEventBus().unregisterEventListener(this);
        for (Node n : this.nodes.values()) {
            n.destroy();
        }
    }

    public void render(float interpolationTime) {
        ComponentRenderer renderer = ClientGUIContext.COMPONENT_RENDERER.get(this.getClass());
        if (renderer != null) {
            renderer.render(this);
        }
        for (Node node : this.nodes.values()) {
            GLUtil.checkError("pre child render:" + node.getId());
            node.render(interpolationTime);
            GLUtil.checkError("post child render:" + node.getId());
        }
    }

    public void tick() {
        for (Node node : this.nodes.values()) {
            node.tick();
        }
    }

    public void addNode(String name, Node node) {
        node.id=name;
        node.setContext(this.screen, this, this.context);
        this.nodes.put(name, node);
    }

    public void addNode(Node node) {
        node.setContext(this.screen, this, this.context);
        this.nodes.put(node.getId(), node);
    }

    public void removeNode(String id) {
        this.nodes.get(id).setParent(null);
        this.nodes.remove(id);
    }

    public Node getNode(String id) {
        return this.nodes.get(id);
    }

    public void deserializeChild(Element element) {
        var s=ClientGUIContext.NODE;

        for (String type : s.keySet()) {
            NodeList elementList = element.getElementsByTagName(type);

            for (int i = 0; i < elementList.getLength(); i++) {
                Element subElement = (Element) elementList.item(i);
                if (subElement.getParentNode() != element) {
                    continue;
                }
                Node n = GUIBuilder.createNode(type, subElement);
                this.nodes.put(n.id, n);
            }
        }
    }

    public MultiMap<String, Node> getNodes() {
        return this.nodes;
    }

    public boolean isMouseInbound() {
        int xm = this.context.getFixedMouseX();
        int ym = this.context.getFixedMouseY();
        int x0 = this.getLayout().getAbsoluteX();
        int x1 = x0 + this.getLayout().getAbsoluteWidth();
        int y0 = this.getLayout().getAbsoluteY();
        int y1 = y0 + this.getLayout().getAbsoluteHeight();
        return xm > x0 && xm < x1 && ym > y0 && ym < y1;
    }

    public Node cloneComponent() {
        return GUIBuilder.createNode(element.getTagName(), element);
    }

    public Node copyComponent(String elementName) {
        Element ele = null;
        for (String type : ClientGUIContext.NODE.keySet()) {
            NodeList elementList = element.getElementsByTagName(type);
            if (ele != null) {
                break;
            }

            for (int i = 0; i < elementList.getLength(); i++) {
                Element e = (Element) elementList.item(i);
                if (e.getAttribute("id").equals(elementName)) {
                    ele = e;
                    break;
                }
            }
        }
        if (ele == null) {
            return null;
        }
        return GUIBuilder.createNode(ele.getTagName(), ele);
    }
}
