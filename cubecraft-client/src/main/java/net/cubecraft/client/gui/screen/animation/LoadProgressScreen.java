package net.cubecraft.client.gui.screen.animation;

import net.cubecraft.client.gui.screen.ScreenBackgroundType;

public final class LoadProgressScreen extends AnimationScreen {

    public LoadProgressScreen() {
        super(false, "cubecraft:_load_progress", ScreenBackgroundType.IMAGE_BACKGROUND);
    }

    @Override
    public boolean isAnimationNotCompleted() {
        return false;
    }
}
