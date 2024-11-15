package net.cubecraft.client.gui.font;

import me.gb2022.commons.math.MathHelper;
import net.cubecraft.text.TextComponent;

public interface FontRenderer {
    TrueTypeFontRenderer FONT_RENDERER = new TrueTypeFontRenderer();
    TrueTypeFontRenderer ICON_FONT_RENDERER = new TrueTypeFontRenderer();


    static TrueTypeFontRenderer ttf() {
        return FONT_RENDERER;
    }

    static TrueTypeFontRenderer icon() {
        return ICON_FONT_RENDERER;
    }


    default void renderShadow(String s, int x, int y, int color, int size, FontAlignment alignment) {
        render(s, (int) (x + 0.125 * size), (int) (y + 0.125 * size), MathHelper.clamp(color - 0xbbbbbb, 0, 0xFFFFFF), size, 0, alignment);
        render(s, x, y, color, size, 0, alignment);
    }

    default void render(String string, int x, int y, int color, int size, double z, FontAlignment alignment) {
        render(TextComponent.create(string).size(size).color(color), x, y, z, alignment);
    }

    default void gc(){
    }

    void render(TextComponent text, int x, int y, double z, FontAlignment alignment);

    default void resize() {
    }
}
