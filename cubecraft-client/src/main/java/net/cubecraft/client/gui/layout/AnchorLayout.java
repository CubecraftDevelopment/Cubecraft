package net.cubecraft.client.gui.layout;

import me.gb2022.commons.registry.TypeItem;

/**
 * JavaFX styled AnchorLayout.
 *
 * @author GrassBlock2022
 */
@TypeItem("anchor")
public final class AnchorLayout extends Layout {
    private int left, right, bottom, top;
    private int prefX, prefY, prefWidth, prefHeight;

    @Override
    public void initialize(String[] metadata) {
        this.prefX = Integer.parseInt(metadata[0]);
        this.prefY = Integer.parseInt(metadata[1]);
        this.prefWidth = Integer.parseInt(metadata[2]);
        this.prefHeight = Integer.parseInt(metadata[3]);
        this.left = Integer.parseInt(metadata[4]);
        this.right = Integer.parseInt(metadata[5]);
        this.top = Integer.parseInt(metadata[6]);
        this.bottom = Integer.parseInt(metadata[7]);
    }

    @Override
    public void resize(int x, int y, int scrWidth, int scrHeight) {
        var top = -999;
        var bottom = -999;
        var left = -999;
        var right = -999;

        var TF = this.top == -1;
        var BF = this.bottom == -1;
        var LF = this.left == -1;
        var RF = this.right == -1;

        if (!LF && RF) {
            left = x + this.left;
            right = left + this.prefWidth;
        } else if (LF && !RF) {
            right = x + scrWidth - this.right;
            left = right - this.prefWidth;
        } else {
            left = x + this.left;
            right = x + scrWidth - this.right;
        }

        if (!TF && BF) {
            top = y + this.top;
            bottom = top + this.prefHeight;
        } else if (TF && !BF) {
            bottom = y + scrHeight - this.bottom;
            top = bottom - this.prefHeight;
        } else {
            top = y + this.top;
            bottom = y + scrHeight - this.bottom;
        }

        this.setAbsoluteX(left);
        this.setAbsoluteY(top);
        this.setAbsoluteWidth(right - left);
        this.setAbsoluteHeight(bottom - top);

        if (TF && BF && LF && RF) {
            this.setAbsoluteX(this.prefX + x);
            this.setAbsoluteY(this.prefY + y);
            this.setAbsoluteWidth(this.prefWidth);
            this.setAbsoluteHeight(this.prefHeight);
        }
        if (TF && BF) {
            this.setAbsoluteY(this.prefY + y);
            this.setAbsoluteHeight(this.prefHeight);
        }
        if (LF && RF) {
            this.setAbsoluteX(this.prefX + x);
            this.setAbsoluteWidth(this.prefWidth);
        }
    }

    public void setPrefWidth(int prefWidth) {
        this.prefWidth = prefWidth;
    }

    public void setPrefHeight(int prefHeight) {
        this.prefHeight = prefHeight;
    }

    public void setPrefX(int prefX) {
        this.prefX = prefX;
    }

    public void setPrefY(int prefY) {
        this.prefY = prefY;
    }

    public int getPrefWidth() {
        return prefWidth;
    }

    public int getPrefHeight() {
        return prefHeight;
    }

    public int getPrefX() {
        return prefX;
    }

    public int getPrefY() {
        return prefY;
    }

    public void setLeft(int left) {
        this.left = left;
    }

    public void setRight(int right) {
        this.right = right;
    }

    public void setTop(int top) {
        this.top = top;
    }

    public void setBottom(int bottom) {
        this.bottom = bottom;
    }
}
