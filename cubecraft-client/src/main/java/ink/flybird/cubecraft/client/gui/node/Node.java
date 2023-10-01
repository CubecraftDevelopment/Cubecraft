package ink.flybird.cubecraft.client.gui.node;

import ink.flybird.cubecraft.client.gui.base.Text;
import ink.flybird.cubecraft.client.gui.GUIManager;
import ink.flybird.cubecraft.client.gui.GUIRegistry;
import ink.flybird.cubecraft.client.gui.screen.Screen;
import ink.flybird.cubecraft.client.gui.ComponentRenderer;
import ink.flybird.cubecraft.client.gui.font.FontAlignment;
import ink.flybird.cubecraft.client.gui.layout.Layout;
import ink.flybird.quantum3d_legacy.GLUtil;
import ink.flybird.fcommon.container.MultiMap;

import ink.flybird.fcommon.file.DocumentUtil;
import ink.flybird.fcommon.file.XmlReader;
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
    protected GUIManager context;


    public void init(Element element) {
        if (element == null) {
            throw new IllegalArgumentException("null element for deserializing node!");
        }
        this.element = element;
        this.style = DocumentUtil.getAttribute(element, "style", "default");
        this.id = DocumentUtil.getAttribute(element, "id", String.valueOf(element.hashCode()));
        this.layout = GUIRegistry.createLayout(element.getAttribute("layout"), element.getAttribute("border"));

        this.deserializeChild(element);
    }

    public void setContext(Screen screen, Node parent, GUIManager context) {
        this.screen = screen;
        this.parent = parent;
        this.context = context;
        this.context.getDeviceEventBus().registerEventListener(this);
        for (Node n : nodes.values()) {
            n.setContext(screen, parent, context);
        }
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

    public GUIManager getContext() {
        return context;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
        ComponentRenderer renderer = this.context.getRenderController(this.getClass());
        if (renderer != null) {
            renderer.render(this);
        }
        for (Node node : this.nodes.values()) {
            GLUtil.checkError("pre child shortTick:" + node.id);
            node.render(interpolationTime);
            GLUtil.checkError("post child shortTick:" + node.id);
        }
    }

    public void tick() {
        for (Node node : this.nodes.values()) {
            node.tick();
        }
    }

    public void addNode(String name, Node node) {
        node.setContext(this.screen, this, this.context);
        this.nodes.put(name, node);
    }

    public void removeNode(String id) {
        this.nodes.get(id).setParent(null);
        this.nodes.remove(id);
    }

    public Node getNode(String id) {
        return this.nodes.get(id);
    }

    public void deserializeChild(Element element) {
        for (String type : GUIRegistry.NODE.getMap().keySet()) {
            NodeList elementList = element.getElementsByTagName(type);

            for (int i = 0; i < elementList.getLength(); i++) {
                Element subElement = (Element) elementList.item(i);
                if (subElement.getParentNode() != element) {
                    continue;
                }
                Node n = GUIRegistry.createNode(type, subElement);
                this.nodes.put(n.id, n);
            }
        }
    }

    public MultiMap<String, Node> getNodes() {
        return this.nodes;
    }

    public boolean isMouseInbound(){
        int xm = this.context.getFixedMouseX();
        int ym = this.context.getFixedMouseY();
        int x0 = this.getLayout().getAbsoluteX();
        int x1 = x0 + this.getLayout().getAbsoluteWidth();
        int y0 = this.getLayout().getAbsoluteY();
        int y1 = y0 + this.getLayout().getAbsoluteHeight();
        return xm > x0 && xm < x1 && ym > y0 && ym < y1;
    }
}
