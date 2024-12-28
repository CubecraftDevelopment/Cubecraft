package net.cubecraft.text;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.Objects;

public class TextComponent {
    public static final TextComponent EMPTY = text("");
    private String content;
    private int color;
    private boolean bold;
    private boolean italic;
    private boolean delete;

    private TextComponent next;
    private TextComponent prev;

    private boolean underline;
    private float size;

    public TextComponent(String content, float size, int color, boolean bold, boolean italic, boolean delete, boolean underline) {
        this.content = content;
        this.color = color;
        this.bold = bold;
        this.italic = italic;
        this.delete = delete;
        this.underline = underline;
        this.size = size;
    }

    public static TextComponent text(Object text) {
        return new TextComponent(String.valueOf(text), 8, 0xFFFFFF, false, false, false, false);
    }

    public static TextComponent text(String text) {
        return new TextComponent(text, 8, 0xFFFFFF, false, false, false, false);
    }

    public static TranslatableComponent translatable(String text) {
        return new TranslatableComponent(text, 8, 0xFFFFFF, false, false, false, false);
    }

    public static IconFontComponent iconFont(String point) {
        return new IconFontComponent(point, 8, 0xFFFFFF, false, false, false, false);
    }

    public static TextComponent deserialize(JsonObject json) {
        var dom = json.getAsJsonObject();

        var color = dom.has("color") ? parseString(dom.get("color").getAsString()) : 0xFFFFFF;
        var italic = dom.has("italic") && dom.get("italic").getAsBoolean();
        var bold = dom.has("bold") && dom.get("bold").getAsBoolean();
        var delete = dom.has("delete") && dom.get("delete").getAsBoolean();
        var underline = dom.has("underline") && dom.get("underline").getAsBoolean();
        var size = dom.has("size") ? dom.get("size").getAsFloat() : 8F;

        var component = ((TextComponent) null);

        if (dom.has("text")) {
            component = TextComponent.text(dom.get("text").getAsString());
        }
        if (dom.has("translate")) {
            component = TextComponent.translatable(dom.get("translate").getAsString());
        }
        if (dom.has("icon")) {
            component = TextComponent.iconFont(dom.get("icon").getAsString());
        }

        if (component == null) {
            throw new IllegalArgumentException("unknown Component type->" + json);
        }

        component.color(color);
        component.bold(bold);
        component.italic(italic);
        component.delete(delete);
        component.underline(underline);
        component.size(size);

        if (dom.has("next")) {
            component.append(deserialize(dom.getAsJsonObject("next")));
        }

        return component;
    }

    public static TextComponent resolveJson(String json) {
        return deserialize(JsonParser.parseString(json).getAsJsonObject());
    }

    public static int hash(String text, float size, int color, boolean bold, boolean italic, boolean delete, boolean underline) {
        int result = 17; // 任意素数起始值
        result = 31 * result + (text != null ? text.hashCode() : 0);
        result = 31 * result + color;
        result = 31 * result + (bold ? 1 : 0);
        result = 31 * result + (italic ? 1 : 0);
        result = 31 * result + (delete ? 1 : 0);
        result = 31 * result + Float.floatToRawIntBits(size);
        return 31 * result + (underline ? 1 : 0);
    }

    public static int parseString(String str) {
        if (str.startsWith("#")) {
            return Integer.parseInt(str.substring(1), 16);
        }
        if (str.startsWith("0")) {
            return Integer.parseInt(str.substring(1), 8);
        }
        return Integer.parseInt(str, 10);
    }

    public String getContent() {
        return content;
    }

    public TextComponent content(String text) {
        this.content = text;
        return this;
    }

    public TextComponent size(float size) {
        this.size = size;
        return this;
    }

    public int getColor() {
        return color;
    }

    public TextComponent color(int color) {
        this.color = color;
        return this;
    }

    public boolean isBold() {
        return bold;
    }

    public TextComponent bold(boolean bold) {
        this.bold = bold;
        return this;
    }

    public boolean isDelete() {
        return delete;
    }

    public TextComponent delete(boolean delete) {
        this.delete = delete;
        return this;
    }

    public TextComponent italic(boolean italic) {
        this.italic = italic;
        return this;
    }

    public boolean isItalic() {
        return italic;
    }

    public TextComponent underline(boolean underline) {
        this.underline = underline;
        return this;
    }

    public TextComponent append(TextComponent append) {
        this.next = append;
        append.prev = this;
        return append;
    }

    public TextComponent previous(TextComponent append) {
        this.prev = append;
        append.next = this;
        return append;
    }

    public TextComponent getNext() {
        return next;
    }

    public TextComponent getPrev() {
        return prev;
    }

    @Override
    public int hashCode() {
        return hash(content, size, color, bold, italic, delete, underline);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof TextComponent other)) {
            return false;
        }
        return Objects.equals(
                other.content,
                this.content
        ) && other.bold == this.bold && other.italic == this.italic && other.delete == this.delete && other.underline == this.underline;
    }

    @Override
    public String toString() {
        return this.content + this.next.toString();
    }

    public boolean isUnderline() {
        return this.underline;
    }

    public boolean isEmpty() {
        if (this.content == null) {
            return true;
        }
        return this.content.isEmpty();
    }

    public float getSize() {
        return this.size;
    }

    public TextComponent getFirst() {
        if (getPrev() == null) {
            return this;
        }
        return this.getPrev().getFirst();
    }

    public TextComponent getLast() {
        if (getNext() == null) {
            return this;
        }
        return this.getNext().getLast();
    }


    public TranslatableComponent translatable() {
        return ((TranslatableComponent) this);
    }
}
