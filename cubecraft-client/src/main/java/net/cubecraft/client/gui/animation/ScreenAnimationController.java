package net.cubecraft.client.gui.animation;

import net.cubecraft.client.gui.base.DisplayScreenInfo;
import net.cubecraft.client.gui.screen.Screen;

public interface ScreenAnimationController {
    void tick(int partial);

    void render(Screen screen, Screen next, DisplayScreenInfo info, float delta);

    boolean isDone();
}
