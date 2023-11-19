package net.cubecraft.client.gui.font;

public interface IFontRenderer {
    void renderShadow(String s, int x, int y, int color, int size, FontAlignment alignment);

    void render(String string, int x, int y, int color, int size, double z, FontAlignment alignment);
}
