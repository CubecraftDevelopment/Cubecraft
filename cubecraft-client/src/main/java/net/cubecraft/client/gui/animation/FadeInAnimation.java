package net.cubecraft.client.gui.animation;

import net.cubecraft.client.gui.base.DisplayScreenInfo;
import net.cubecraft.client.gui.screen.Screen;

public final class FadeInAnimation implements ScreenAnimationController {
    private float alpha = 0;

    @Override
    public void tick(int partial) {

    }

    @Override
    public void render(Screen screen, Screen next, DisplayScreenInfo info, float delta) {

    }

    @Override
    public boolean isDone() {
        return false;
    }


}
