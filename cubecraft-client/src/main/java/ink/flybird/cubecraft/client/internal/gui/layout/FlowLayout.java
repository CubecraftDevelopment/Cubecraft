package ink.flybird.cubecraft.client.internal.gui.layout;

import ink.flybird.cubecraft.client.gui.layout.Layout;

public final class FlowLayout extends Layout {
    private FlowSide side;
    private int pos, length;

    public FlowLayout(){}

    @Override
    public void initialize(String[] metadata) {
        this.side=FlowSide.fromID(metadata[0]);
        this.pos= Integer.parseInt(metadata[1]);
        this.length=Integer.parseInt(metadata[2]);
    }

    @Override
    public void resize(int x, int y, int scrWidth, int scrHeight) {
        switch (this.side) {
            case TOP -> {
                this.absoluteY = y + pos;
                this.absoluteWidth = scrWidth;
                this.absoluteX = x;
                this.absoluteHeight = length;
            }
            case BOTTOM -> {
                this.absoluteY = x + scrHeight - pos;
                this.absoluteWidth = scrWidth;
                this.absoluteX = x;
                this.absoluteHeight = length;
            }
            case LEFT -> {
                this.absoluteX = x + pos;
                this.absoluteHeight = scrHeight;
                this.absoluteY = y;
                this.absoluteWidth = length;
            }
            case RIGHT -> {
                this.absoluteX = x + scrWidth - pos;
                this.absoluteHeight = scrHeight;
                this.absoluteY = y;
                this.absoluteWidth = length;
            }
        }


        if (this.scale.left()) {
            this.absoluteX = 0;
        }
        if (this.scale.right()) {
            this.absoluteWidth = scrWidth - this.absoluteX;
        }
        if (this.scale.bottom()) {
            this.absoluteHeight = scrHeight - this.absoluteY;
        }
        if (this.scale.top()) {
            this.absoluteY = 0;
        }

        this.absoluteX += this.getBorder().left();
        this.absoluteY += this.getBorder().top();
        this.absoluteWidth -= this.getBorder().right()+this.getBorder().left();
        this.absoluteHeight -= this.getBorder().bottom()+this.getBorder().top();
    }

    public enum FlowSide {
        LEFT,
        RIGHT,
        TOP,
        BOTTOM;

        public static FlowSide fromID(String id) {
            return switch (id) {
                case "left" -> LEFT;
                case "right" -> RIGHT;
                case "bottom" -> BOTTOM;
                case "top" -> TOP;
                default -> throw new IllegalArgumentException("no matched constant named " + id);
            };
        }
    }
}