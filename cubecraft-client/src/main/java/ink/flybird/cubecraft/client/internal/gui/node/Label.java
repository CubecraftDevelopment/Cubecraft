package ink.flybird.cubecraft.client.internal.gui.node;


import ink.flybird.cubecraft.client.ClientSharedContext;
import ink.flybird.cubecraft.client.gui.base.Text;
import ink.flybird.cubecraft.client.gui.font.FontAlignment;
import ink.flybird.cubecraft.client.gui.layout.Layout;
import ink.flybird.cubecraft.client.gui.node.Node;
import ink.flybird.cubecraft.SharedContext;
import ink.flybird.fcommon.math.MathHelper;
import org.w3c.dom.Element;

public class Label extends Node {
    public Text text2;
    public int color;
    private String text;
    private FontAlignment alignment;

    @Override
    public void init(Element element) {
        super.init(element);
        this.text = switch (element.getAttribute("type")){
            case "lang"-> SharedContext.I18N.get(element.getTextContent().trim());
            default -> element.getTextContent().trim();
        };
        this.color = MathHelper.hex2Int(element.getAttribute("color"));
        this.alignment = FontAlignment.from(element.getAttribute("align"));
    }

    @Override
    public void render(float interpolationTime) {
        Layout layout = this.getLayout();
        ClientSharedContext.SMOOTH_FONT_RENDERER.render(this.text, layout.getAbsoluteX() + layout.getAbsoluteWidth() / 2, layout.getAbsoluteY(), this.color, layout.getAbsoluteHeight(), layout.layer, this.alignment);
    }

    @Override
    public void onResize(int x, int y, int w, int h) {
        this.layout.resize(x, y, w, h);
    }

    public Text getText() {
        return text2;
    }

    public void setText(Text text) {
        this.text=text.getText();
        this.color=text.getColor();
        this.alignment=text.getAlignment();
    }
}