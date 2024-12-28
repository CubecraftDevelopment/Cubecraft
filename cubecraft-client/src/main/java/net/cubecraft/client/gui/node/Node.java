package net.cubecraft.client.gui.node;

import me.gb2022.commons.container.MultiMap;
import me.gb2022.commons.file.DocumentUtil;
import me.gb2022.commons.file.XmlReader;
import me.gb2022.quantum3d.device.Keyboard;
import me.gb2022.quantum3d.device.KeyboardButton;
import me.gb2022.quantum3d.device.Mouse;
import me.gb2022.quantum3d.device.MouseButton;
import me.gb2022.quantum3d.util.GLUtil;
import net.cubecraft.client.CubecraftClient;
import net.cubecraft.client.context.ClientGUIContext;
import net.cubecraft.client.gui.ComponentRenderer;
import net.cubecraft.client.gui.GUIBuilder;
import net.cubecraft.client.gui.event.component.ComponentInitializeEvent;
import net.cubecraft.client.gui.font.FontAlignment;
import net.cubecraft.client.gui.layout.Layout;
import net.cubecraft.client.gui.layout.Scale;
import net.cubecraft.client.gui.screen.Screen;
import net.cubecraft.text.TextComponent;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;

public abstract class Node {
    protected final MultiMap<String, Node> nodes = new MultiMap<>();
    protected Element element;

    protected String style;
    protected Layout layout;
    protected String id;
    protected Node parent;
    protected Screen screen;
    protected ClientGUIContext context;
    protected boolean visible = true;

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

    public final void setContext(Screen screen, Node parent, ClientGUIContext context) {
        this.screen = screen;
        this.parent = parent;
        this.context = context;
        for (Node n : nodes.values()) {
            n.setContext(screen, parent, context);
        }
        this.context.getEventBus().callEvent(new ComponentInitializeEvent(this), this.getId());
        CubecraftClient.getInstance().getClientEventBus().callEvent(new ComponentInitializeEvent(this));
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
        return this.style+":normal";
    }

    public TextComponent queryText(String query) {
        return null;
    }

    public FontAlignment queryTextAlignment(String query) {
        return FontAlignment.MIDDLE;
    }

    public double queryNum(String query) {
        return 0;
    }


    public void onResize(int x, int y, int w, int h) {
        if (!isVisible()) {
            x = -999999;
            y = -999999;
            w = 1;
            h = 1;//cannot see whatever
        }

        var renderer = getRenderer();

        var xo = 0;
        var yo = 0;


        if (renderer != null) {
            var offset = renderer.getOffset(this);

            xo = offset.x();
            yo = offset.y();
        }

        if (this.layout != null) {
            this.layout.resize(x, y, w, h);
            for (Node node : this.nodes.values()) {
                node.onResize(
                        this.layout.getAbsoluteX() + xo,
                        this.layout.getAbsoluteY() + yo,
                        this.layout.getAbsoluteWidth(),
                        this.layout.getAbsoluteHeight()
                );
            }
        } else {
            for (Node node : this.nodes.values()) {
                node.onResize(x + xo, y + yo, w, h);
            }
        }
    }

    public void destroy() {
        for (Node n : this.nodes.values()) {
            n.destroy();
        }
    }

    public void init() {
        this.context.getDeviceEventBus().registerEventListener(this);
        for (Node n : this.nodes.values()) {
            n.init();
        }
    }

    public void render(float interpolationTime) {
        if (!isVisible()) {
            return;
        }
        var renderer = getRenderer();
        if (renderer != null) {
            renderer.render(this);
        }
        for (Node node : this.nodes.values()) {
            GLUtil.checkError("pre child render:" + node.getId());
            node.render(interpolationTime);
            GLUtil.checkError("post child render:" + node.getId());
        }
    }

    public final ComponentRenderer getRenderer() {
        return ClientGUIContext.COMPONENT_RENDERER.get(this.getClass());
    }

    public void tick() {
        if (!isVisible()) {
            return;
        }
        for (Node node : this.nodes.values()) {
            node.tick();
        }
    }

    public void addNode(String name, Node node) {
        node.id = name;
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

    public final void deserializeLayout(Element element, XmlReader reader) {
        this.setLayout(reader.deserialize((Element) element.getElementsByTagName("layout").item(0), Layout.class));
    }

    public final void deserializeChild(Element element) {
        var s = ClientGUIContext.NODE;

        for (String type : s.keySet()) {
            var elementList = element.getElementsByTagName(type);

            for (int i = 0; i < elementList.getLength(); i++) {
                var subElement = (Element) elementList.item(i);

                if (subElement.getParentNode() != element) {
                    continue;
                }

                var n = GUIBuilder.createNode(type, subElement);
                this.nodes.put(n.id, n);
            }
        }
    }

    public final MultiMap<String, Node> getNodes() {
        if (!isVisible()) {
            return new MultiMap<>();
        }
        return this.nodes;
    }

    public final boolean isMouseInbound(double fx, double fy) {
        if (!isVisible()) {
            return false;
        }
        var x0 = this.getLayout().getAbsoluteX();
        var x1 = x0 + this.getLayout().getAbsoluteWidth();
        var y0 = this.getLayout().getAbsoluteY();
        var y1 = y0 + this.getLayout().getAbsoluteHeight();
        return fx > x0 && fx < x1 && fy > y0 && fy < y1;
    }

    public boolean isMouseInbound() {
        return false;
    }

    public final Node cloneComponent() {
        return GUIBuilder.createNode(element.getTagName(), element);
    }

    public final Node copyComponent(String elementName) {
        Element ele = null;
        for (String type : ClientGUIContext.NODE.keySet()) {
            NodeList elementList = this.element.getElementsByTagName(type);
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
        if (Objects.equals(elementName, "_ROOT")) {
            ele = this.element;
        }
        if (ele == null) {
            return null;
        }
        return GUIBuilder.createNode(ele.getTagName(), ele);
    }

    public final <C extends Node> Optional<C> getNodeOptional(String id, Class<C> clazz) {
        if (!isVisible()) {
            return Optional.empty();
        }
        return Optional.ofNullable(clazz.cast(this.nodes.get(id)));
    }


    public void onMouseClicked(Mouse m, int fx, int fy, MouseButton button) {
        if (!isVisible()) {
            return;
        }
        for (Node node : this.nodes.values()) {
            node.onMouseClicked(m, fx, fy, button);
        }
    }

    public void onKeyboardPressed(Keyboard keyboard, KeyboardButton button) {
        if (!isVisible()) {
            return;
        }
        for (Node node : this.nodes.values()) {
            node.onKeyboardPressed(keyboard, button);
        }
    }

    public void onKeyboardChar(Keyboard keyboard, char c) {
        if (!isVisible()) {
            return;
        }
        for (Node node : this.nodes.values()) {
            node.onKeyboardChar(keyboard, c);
        }
    }

    public void onMousePosition(Mouse m, int fx, int fy) {
        if (!isVisible()) {
            return;
        }
        for (Node node : this.nodes.values()) {
            node.onMousePosition(m, fx, fy);
        }
    }

    public void collectNodes(BiConsumer<String, Node> consumer) {
        if (!isVisible()) {
            return;
        }
        consumer.accept(this.getId(), this);
        for (var id : this.nodes.keySet()) {
            this.nodes.get(id).collectNodes(consumer);
        }
    }

    public Map<String, Node> collectNodes() {
        var nodes = new HashMap<String, Node>();
        collectNodes(nodes::put);
        return nodes;
    }

    public void setVisible(boolean b) {
        this.visible = b;
    }

    public boolean isVisible() {
        return visible;
    }

    public Node findNode(String templateWorldInfo) {
        for (var node : collectNodes().values()) {
            if (Objects.equals(node.getId(), templateWorldInfo)) {
                return node;
            }
        }

        return null;
    }
}
