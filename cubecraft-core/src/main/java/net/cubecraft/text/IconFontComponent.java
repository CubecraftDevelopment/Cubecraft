package net.cubecraft.text;

public final class IconFontComponent extends TextComponent {
    private char icon;

    public IconFontComponent(String content, float size, int color, boolean bold, boolean italic, boolean delete, boolean underline) {
        super(content, size, color, bold, italic, delete, underline);
        content(content);
    }

    @Override
    public TextComponent content(String text) {
        this.icon = (char) Integer.parseInt(text, 16);
        return super.content(text);
    }

    @Override
    public String getContent() {
        return String.valueOf(this.icon);
    }
}
