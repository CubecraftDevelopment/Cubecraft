package net.cubecraft.client.gui.screen.animation;

import me.gb2022.quantum3d.util.Sync;
import net.cubecraft.client.gui.base.DisplayScreenInfo;
import net.cubecraft.client.gui.screen.Screen;
import net.cubecraft.client.gui.screen.ScreenBackgroundType;

public abstract class AnimationScreen extends Screen {
    protected int time;

    public AnimationScreen(boolean grabMouse, String id, ScreenBackgroundType type) {
        super(grabMouse, id, type);
    }


    @Override
    public void render(DisplayScreenInfo info, float deltaTime, float alphaOverwrite) {
        Sync.sync(100);
        if(this.isAnimationNotCompleted()) {
            ++this.time;
        }
    }

    public abstract boolean isAnimationNotCompleted();
}
