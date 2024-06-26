package net.cubecraft.client.gui.layout;

import me.gb2022.commons.registry.TypeItem;

@TypeItem("flow")
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
                this.setAbsoluteY(y + pos);
                this.setAbsoluteWidth(scrWidth);
                this.setAbsoluteX(x);
                this.setAbsoluteHeight(length);
            }
            case BOTTOM -> {
                this.setAbsoluteY(x + scrHeight - pos);
                this.setAbsoluteWidth(scrWidth);
                this.setAbsoluteX(x);
                this.setAbsoluteHeight(length);
            }
            case LEFT -> {
                this.setAbsoluteX(x + pos);
                this.setAbsoluteHeight(scrHeight);
                this.setAbsoluteY(y);
                this.setAbsoluteWidth(length);
            }
            case RIGHT -> {
                this.setAbsoluteX(x + scrWidth - pos);
                this.setAbsoluteHeight(scrHeight);
                this.setAbsoluteY(y);
                this.setAbsoluteWidth(length);
            }
        }


        if (this.scale.left()) {
            this.setAbsoluteX(0);
        }
        if (this.scale.right()) {
            this.setAbsoluteWidth(scrWidth - this.getAbsoluteX());
        }
        if (this.scale.bottom()) {
            this.setAbsoluteHeight(scrHeight - this.getAbsoluteY());
        }
        if (this.scale.top()) {
            this.setAbsoluteY(0);
        }

        this.setAbsoluteX(this.getAbsoluteX() + this.getBorder().left());
        this.setAbsoluteY(this.getAbsoluteY() + this.getBorder().top());
        this.setAbsoluteWidth(this.getAbsoluteWidth() - (this.getBorder().right()+this.getBorder().left()));
        this.setAbsoluteHeight(this.getAbsoluteHeight() - (this.getBorder().bottom()+this.getBorder().top()));
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