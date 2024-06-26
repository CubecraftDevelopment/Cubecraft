package net.cubecraft.client.gui.node;


import net.cubecraft.SharedContext;
import net.cubecraft.client.context.ClientGUIContext;
import net.cubecraft.client.gui.base.Text;
import net.cubecraft.client.gui.font.FontAlignment;
import net.cubecraft.client.gui.layout.Layout;
import me.gb2022.commons.math.MathHelper;
import me.gb2022.commons.registry.TypeItem;
import org.w3c.dom.Element;

@TypeItem("label")
public class Label extends Node {
    public Text text2;
    public int color;
    private String text;
    private FontAlignment alignment;

    @Override
    public void init(Element element) {
        super.init(element);
        this.text = switch (element.getAttribute("type")) {
            case "lang" -> SharedContext.I18N.get(element.getTextContent().trim());
            default -> element.getTextContent().trim();
        };
        this.color = MathHelper.hex2Int(element.getAttribute("color"));
        this.alignment = FontAlignment.from(element.getAttribute("align"));
    }

    @Override
    public void render(float interpolationTime) {
        Layout layout = this.getLayout();
        ClientGUIContext.FONT_RENDERER.render(this.text, layout.getAbsoluteX() + layout.getAbsoluteWidth() / 2, layout.getAbsoluteY(), this.color, layout.getAbsoluteHeight(), layout.layer, this.alignment);
    }

    @Override
    public void onResize(int x, int y, int w, int h) {
        this.layout.resize(x, y, w, h);
    }

    public Text getText() {
        return text2;
    }

    public void setText(Text text) {
        this.text = text.getText();
        this.color = text.getColor();
        this.alignment = text.getAlignment();
    }

    public void setText(String text) {
        this.text = text;
    }
}