package net.cubecraft.client.gui.layout;

import me.gb2022.commons.registry.TypeItem;

@TypeItem("viewport")
public final class ViewportLayout extends Layout {
    private float left;
    private float right;
    private float bottom;
    private float top;

    @Override
    public void initialize(String[] metadata) {
        this.left = Float.parseFloat(metadata[0]);
        this.right = Float.parseFloat(metadata[1]);
        this.bottom = Float.parseFloat(metadata[2]);
        this.top = Float.parseFloat(metadata[3]);
    }

    @Override
    public void resize(int x, int y, int scrWidth, int scrHeight) {
        this.setAbsoluteX((int) (left / 100 * scrWidth));
        this.setAbsoluteY((int) (top / 100 * scrHeight));
        this.width = (int) (right / 100 * scrWidth - getAbsoluteX());
        this.height = (int) (bottom / 100 * scrHeight - getAbsoluteY());
        this.setAbsoluteX(this.getAbsoluteX() + this.getBorder().left());
        this.setAbsoluteY(this.getAbsoluteY() + this.getBorder().top());
        this.width -= this.getBorder().right() * 2;
        this.height -= this.getBorder().bottom() * 2;

        if (this.scale.left()) {
            this.setAbsoluteX(x);
        }
        if (this.scale.right()) {
            this.setAbsoluteWidth(scrWidth - this.getAbsoluteX() + x);
        } else {
            this.setAbsoluteWidth(this.width);
        }
        if (this.scale.bottom()) {
            this.setAbsoluteHeight(scrHeight - this.getAbsoluteY() + y);
        } else {
            this.setAbsoluteHeight(this.height);
        }
        if (this.scale.top()) {
            this.setAbsoluteY(y);
        }

        this.setAbsoluteX(this.getAbsoluteX() + this.getBorder().left());
        this.setAbsoluteY(this.getAbsoluteY() + this.getBorder().top());
        this.setAbsoluteWidth(this.getAbsoluteWidth() - (this.getBorder().right() + this.getBorder().left()));
        this.setAbsoluteHeight(this.getAbsoluteHeight() - (this.getBorder().bottom() + this.getBorder().top()));
    }
}
