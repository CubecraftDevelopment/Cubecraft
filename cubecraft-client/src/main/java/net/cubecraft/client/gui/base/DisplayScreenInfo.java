package net.cubecraft.client.gui.base;

public final class DisplayScreenInfo {
    private final int scrWidth;
    private final int scrHeight;
    private final int centerX;
    private final int centerY;

    public DisplayScreenInfo(int scrWidth, int scrHeight, int centerX, int centerY) {
        this.scrWidth = scrWidth;
        this.scrHeight = scrHeight;
        this.centerX = centerX;
        this.centerY = centerY;
    }

    public int getCenterX() {
        return centerX;
    }

    public int getCenterY() {
        return centerY;
    }

    public int getScreenWidth() {
        return scrWidth;
    }

    public int getScreenHeight() {
        return scrHeight;
    }
}
