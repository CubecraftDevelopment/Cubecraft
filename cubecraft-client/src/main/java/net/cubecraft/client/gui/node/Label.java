package net.cubecraft.client.gui.node;


import me.gb2022.commons.math.MathHelper;
import me.gb2022.commons.registry.TypeItem;
import net.cubecraft.SharedContext;
import net.cubecraft.client.gui.base.Text;
import net.cubecraft.client.gui.font.FontAlignment;
import net.cubecraft.client.gui.font.FontRenderer;
import net.cubecraft.client.gui.layout.Layout;
import net.cubecraft.text.TextComponent;
import org.w3c.dom.Element;

@TypeItem("label")
public class Label extends Node {
    public Text text2;

    @Override
    public void init(Element element) {
        super.init(element);
        var text = switch (element.getAttribute("type")) {
            case "lang" -> SharedContext.I18N.get(element.getTextContent().trim());
            default -> element.getTextContent().trim();
        };

        var color = MathHelper.hex2Int(element.getAttribute("color"));
        var alignment = FontAlignment.from(element.getAttribute("align"));

        this.text2 = new Text(text, color, alignment);
    }

    @Override
    public void render(float interpolationTime) {
        Layout layout = this.getLayout();
        FontRenderer.ttf().render(
                this.text2.getText(),
                layout.getAbsoluteX() + layout.getAbsoluteWidth() / 2,
                layout.getAbsoluteY(),
                layout.layer,
                this.text2.getAlignment()
        );
    }

    @Override
    public void onResize(int x, int y, int w, int h) {
        this.layout.resize(x, y, w, h);
        this.text2.getText().size(this.getLayout().getAbsoluteHeight());
    }

    public Text getText() {
        return text2;
    }

    public void setText(Text text) {
        this.text2 = text;
    }

    public void setText(String text) {
        this.text2.setText(TextComponent.create(text));
    }
}