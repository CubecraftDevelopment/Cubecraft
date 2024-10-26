package net.cubecraft.text;

import java.util.Objects;

public final class TextComponent {
    private String text;
    private int color;
    private boolean bold;
    private boolean italic;
    private boolean delete;

    private TextComponent next;
    private TextComponent prev;


    private boolean underline;
    private float size;

    public TextComponent(String text, float size, int color, boolean bold, boolean italic, boolean delete, boolean underline) {
        this.text = text;
        this.color = color;
        this.bold = bold;
        this.italic = italic;
        this.delete = delete;
        this.underline = underline;
        this.size = size;
    }

    public static TextComponent create(Object text) {
        return new TextComponent(String.valueOf(text), 8, 0xFFFFFF, false, false, false, false);
    }

    public static TextComponent create(String text) {
        return new TextComponent(text, 8, 0xFFFFFF, false, false, false, false);
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

    public String getText() {
        return text;
    }

    public TextComponent text(String text) {
        this.text = text;
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
        return hash(text, size, color, bold, italic, delete, underline);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof TextComponent other)) {
            return false;
        }
        return Objects.equals(other.text, this.text) && other.bold == this.bold && other.italic == this.italic && other.delete == this.delete && other.underline == this.underline;
    }

    @Override
    public String toString() {
        return this.text + this.next.toString();
    }

    public boolean isUnderline() {
        return this.underline;
    }

    public boolean isEmpty() {
        return this.text.isEmpty();
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
}
