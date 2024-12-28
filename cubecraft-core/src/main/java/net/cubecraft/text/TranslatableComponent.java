package net.cubecraft.text;

import net.cubecraft.SharedContext;

import java.util.Arrays;

public final class TranslatableComponent extends TextComponent {
    private final String ref;

    public TranslatableComponent(String content, float size, int color, boolean bold, boolean italic, boolean delete, boolean underline) {
        super(content, size, color, bold, italic, delete, underline);
        this.ref = content;
        setFormat();
    }

    public void setFormat(Object... args) {
        try {
            content(SharedContext.I18N.get(this.ref, args));
        } catch (Exception e) {
            content("format error:" + this.ref + Arrays.toString(args));
        }
    }
}
