package net.cubecraft.client.gui.node.component;


import me.gb2022.commons.registry.TypeItem;
import net.cubecraft.client.gui.font.FontAlignment;
import net.cubecraft.client.gui.font.FontRenderer;
import net.cubecraft.client.gui.node.Node;
import net.cubecraft.text.IconFontComponent;
import net.cubecraft.text.TextComponent;
import org.w3c.dom.Element;

@TypeItem("label")
public final class Label extends Node {
    private TextComponent text = TextComponent.EMPTY;
    private FontAlignment alignment = FontAlignment.LEFT;

    @Override
    public void init(Element element) {
        super.init(element);
        this.alignment = FontAlignment.from(element.getAttribute("align"));
        this.text = TextComponent.resolveJson(element.getTextContent());
    }

    @Override
    public void render(float interpolationTime) {
        var layout = this.getLayout();
        var x = layout.getAbsoluteX() + layout.getAbsoluteWidth() / 2;
        var y = layout.getAbsoluteY();

        if (this.text instanceof IconFontComponent cc) {
            FontRenderer.icon().render(cc, x, y, layout.layer, this.alignment);
            return;
        }
        FontRenderer.ttf().render(this.text, x, y, layout.layer, this.alignment);
    }

    @Override
    public void onResize(int x, int y, int w, int h) {
        this.layout.resize(x, y, w, h);
        this.text.size(this.getLayout().getAbsoluteHeight());
    }

    public TextComponent getText() {
        return text;
    }

    public TextComponent setText(TextComponent text) {
        this.text = text;
        return text;
    }

    public TextComponent setText(String text) {
        return this.setText(TextComponent.text(text));
    }
}