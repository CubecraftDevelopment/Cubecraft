package ink.flybird.cubecraft.client.internal.gui.layout;

import ink.flybird.cubecraft.client.gui.layout.Layout;

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
        this.absoluteX = (int) (left / 100 * scrWidth);
        this.absoluteY = (int) (top / 100 * scrHeight);
        this.width = (int) (right / 100 * scrWidth - absoluteX);
        this.height = (int) (bottom / 100 * scrHeight - absoluteY);
        this.absoluteX += this.getBorder().left();
        this.absoluteY += this.getBorder().top();
        this.width -= this.getBorder().right() * 2;
        this.height -= this.getBorder().bottom() * 2;

        if (this.scale.left()) {
            this.absoluteX = x;
        }
        if (this.scale.right()) {
            this.absoluteWidth = scrWidth - this.absoluteX + x;
        } else {
            this.absoluteWidth = this.width;
        }
        if (this.scale.bottom()) {
            this.absoluteHeight = scrHeight - this.absoluteY + y;
        } else {
            this.absoluteHeight = this.height;
        }
        if (this.scale.top()) {
            this.absoluteY = y;
        }

        this.absoluteX += this.getBorder().left();
        this.absoluteY += this.getBorder().top();
        this.absoluteWidth -= this.getBorder().right() + this.getBorder().left();
        this.absoluteHeight -= this.getBorder().bottom() + this.getBorder().top();
    }
}
