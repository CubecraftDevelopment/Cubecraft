package net.cubecraft.client.gui.node;

import me.gb2022.commons.event.EventHandler;
import me.gb2022.commons.file.DocumentUtil;
import me.gb2022.commons.file.FAMLDeserializer;
import me.gb2022.commons.file.XmlReader;
import me.gb2022.commons.math.MathHelper;
import me.gb2022.commons.registry.TypeItem;
import ink.flybird.quantum3d_legacy.GLUtil;
import me.gb2022.quantum3d.device.KeyboardButton;
import me.gb2022.quantum3d.device.event.MouseScrollEvent;
import net.cubecraft.client.ClientSettingRegistry;
import net.cubecraft.client.ClientSharedContext;
import net.cubecraft.client.context.ClientGUIContext;
import org.lwjgl.opengl.GL11;
import org.w3c.dom.Element;

@TypeItem("scroll_panel")
public class ScrollPanel extends Node {
    private boolean horizontalEnabled;
    private int xOffset, yOffset;
    private int xMin, xMax, yMin, yMax;
    private double xd, yd;
    private double xo, yo;

    @Override
    public void init(Element element) {
        super.init(element);

        this.horizontalEnabled = DocumentUtil.getAttributeB(element, "horizontalEnabled", false);
        this.xMin = DocumentUtil.getAttributeI(element, "xMin", 0);
        this.xMax = DocumentUtil.getAttributeI(element, "xMax", 0);
        this.yMin = DocumentUtil.getAttributeI(element, "yMin", 0);
        this.yMax = DocumentUtil.getAttributeI(element, "yMax", 0);
    }

    @Override
    public void onResize(int x, int y, int w, int h) {
        this.layout.resize(x, y, w, h);
    }


    @Override
    public void render(float interpolationTime) {
        ClientGUIContext.COMPONENT_RENDERER.get(this.getClass()).render(this);

        int x = (int) MathHelper.linearInterpolate(this.xo, this.xOffset, interpolationTime);
        int y = (int) MathHelper.linearInterpolate(this.yo, this.yOffset, interpolationTime);
        for (Node node : this.nodes.values()) {
            node.onResize(this.layout.getAbsoluteX() + x, this.layout.getAbsoluteY() + y, this.layout.getAbsoluteWidth(), this.layout.getAbsoluteHeight());
        }

        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        double scale = ClientSettingRegistry.getFixedGUIScale();
        int sx = (int) (this.layout.getAbsoluteX() * scale);
        int i = this.context.getWindow().getHeight();
        int i2 = (int) (this.layout.getAbsoluteY() * scale);
        int sy = (int) (i - i2 - this.layout.getAbsoluteHeight() * scale);
        int sw = (int) (this.layout.getAbsoluteWidth() * scale);
        int sh = (int) (this.layout.getAbsoluteHeight() * scale);

        sw = Math.max(sw, 0);
        sh = Math.max(sh, 0);

        GL11.glScissor(sx, sy, sw, sh);
        super.render(interpolationTime);
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
        GLUtil.checkError("");
    }


    public void setHorizontalEnabled(boolean horizontalEnabled) {
        this.horizontalEnabled = horizontalEnabled;
    }

    public void setRange(int xMin, int xMax, int yMin, int yMax) {
        this.xMin = xMin;
        this.xMax = xMax;
        this.yMin = yMin;
        this.yMax = yMax;
    }


    @EventHandler
    public void onScroll(MouseScrollEvent event) {
        if (ClientSharedContext.getClient().getClientDeviceContext().getKeyboard().isKeyDown(KeyboardButton.KEY_LEFT_SHIFT)) {
            if (this.horizontalEnabled) {
                this.xd = 16 * -event.getYOffset();
            }
        } else {
            this.yd = 16 * -event.getYOffset();
        }
    }

    @Override
    public void tick() {
        super.tick();
        this.xo = this.xOffset;
        this.yo = this.yOffset;

        this.xd *= 0.75;
        this.yd *= 0.75;

        this.xOffset = (int) MathHelper.clamp(this.xOffset + this.xd, this.xMin, this.xMax);
        this.yOffset = (int) MathHelper.clamp(this.yOffset + this.yd, this.yMin, this.yMax);
    }

    public static class XMLDeserializer implements FAMLDeserializer<ScrollPanel> {
        @Override
        public ScrollPanel deserialize(Element element, XmlReader reader) {
            ScrollPanel panel = new ScrollPanel();
            panel.deserializeLayout(element, reader);
            if (element.hasAttribute("horizontalEnabled")) {
                panel.setHorizontalEnabled(Boolean.parseBoolean(element.getAttribute("horizontalEnabled")));
            }
            int x0 = 0, x1 = 0, y0 = 0, y1 = 0;
            if (element.hasAttribute("xMin")) {
                x0 = Integer.parseInt(element.getAttribute("xMin"));
            }
            if (element.hasAttribute("xMax")) {
                x1 = Integer.parseInt(element.getAttribute("xMax"));
            }
            if (element.hasAttribute("yMin")) {
                y0 = Integer.parseInt(element.getAttribute("yMin"));
            }
            if (element.hasAttribute("yMax")) {
                y1 = Integer.parseInt(element.getAttribute("yMax"));
            }
            panel.setRange(x0, x1, y0, y1);
            return panel;
        }
    }
}
