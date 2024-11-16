package net.cubecraft.client.gui.animation;

import net.cubecraft.client.gui.base.DisplayScreenInfo;
import net.cubecraft.client.gui.screen.Screen;

public final class NoneAnimation implements ScreenAnimationController {
    @Override
    public void tick(int partial) {
    }

    @Override
    public void render(Screen screen, Screen next, DisplayScreenInfo info, float delta) {
        if (screen != null) {
            screen.render(info, delta, 1.0F);
        }
    }

    @Override
    public boolean isDone() {
        return false;
    }
}
