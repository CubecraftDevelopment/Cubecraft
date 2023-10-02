package ink.flybird.cubecraft.client.gui.screen;

import ink.flybird.cubecraft.client.gui.base.DisplayScreenInfo;
import ink.flybird.quantum3d.lwjgl.deprecated.platform.Sync;

public abstract class AnimationScreen extends Screen {
    protected int time;

    public AnimationScreen(boolean grabMouse, String id, ScreenBackgroundType type) {
        super(grabMouse, id, type);
    }

    @Override
    public void render(DisplayScreenInfo info, float deltaTime) {
        Sync.sync(100);
        if(this.isAnimationNotCompleted()) {
            ++this.time;
        }
    }

    public abstract boolean isAnimationNotCompleted();
}
