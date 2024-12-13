package net.cubecraft.client.gui.screen;

import net.cubecraft.client.gui.base.DisplayScreenInfo;

public interface ScreenAttachment {
    void render(Screen screen, DisplayScreenInfo info, float deltaTime, float alphaOverwrite);

    default void tick() {
    }

    default void release() {
    }
}
