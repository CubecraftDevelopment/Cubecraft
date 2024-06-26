package net.cubecraft.client.gui.node;

import net.cubecraft.client.context.ClientGUIContext;
import net.cubecraft.client.gui.font.FontAlignment;
import me.gb2022.commons.math.MathHelper;
import me.gb2022.commons.registry.TypeItem;
import org.w3c.dom.Element;

@TypeItem("icon")
public class Icon extends Node {
    char icon;
    int color;
    FontAlignment alignment;

    @Override
    public void init(Element element) {
        super.init(element);
        this.icon = (char) MathHelper.hex2Int(element.getTextContent().trim());
        this.color = MathHelper.hex2Int(element.getAttribute("color"));
        this.alignment = FontAlignment.from(element.getAttribute("align"));
    }

    @Override
    public void render(float interpolationTime) {
        ClientGUIContext.ICON_FONT_RENDERER.render(String.valueOf(icon), getLayout().getAbsoluteX(), getLayout().getAbsoluteY(), color, this.layout.getAbsoluteHeight(), 0, alignment);
    }
}
