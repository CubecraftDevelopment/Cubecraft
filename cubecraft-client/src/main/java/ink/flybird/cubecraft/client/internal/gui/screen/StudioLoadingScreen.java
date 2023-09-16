package ink.flybird.cubecraft.client.internal.gui.screen;

import ink.flybird.cubecraft.client.gui.base.DisplayScreenInfo;
import ink.flybird.cubecraft.client.gui.screen.Screen;
import ink.flybird.cubecraft.client.gui.screen.ScreenBackgroundType;
import ink.flybird.quantum3d_legacy.GLUtil;
import ink.flybird.quantum3d_legacy.ShapeRenderer;
import ink.flybird.quantum3d_legacy.draw.VertexBuilder;
import ink.flybird.quantum3d_legacy.draw.VertexBuilderAllocator;
import ink.flybird.quantum3d_legacy.textures.Texture2D;
import ink.flybird.cubecraft.client.internal.registry.ResourceRegistry;
import org.w3c.dom.Element;

public class StudioLoadingScreen extends Screen {
    private static final Texture2D STUDIO_LOGO = new Texture2D(false, false);
    private int time = 0;

    public StudioLoadingScreen(boolean grabMouse, String id, ScreenBackgroundType type, Element e) {
        super(grabMouse, id, type);
    }

    @Override
    public void init() {
        STUDIO_LOGO.load(ResourceRegistry.STUDIO_LOGO);
    }

    @Override
    public void render(DisplayScreenInfo info, float interpolationTime) {
        int xc = info.centerX();
        int yc = info.centerY();
        float alpha = 0f;
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        ++this.time;

        if (this.time < 75) {
            alpha = this.time / 75f;
        }
        if (this.time >= 75 && this.time < 225) {
            alpha = 1f;
        }
        if (this.time >= 225 && this.time <= 300) {
            alpha = 1 - (this.time - 225) / 75f;
        }

        VertexBuilder builder = VertexBuilderAllocator.createByPrefer(64);
        builder.begin();
        builder.color(alpha, alpha, alpha, 1);

        ShapeRenderer.drawRectUV(builder, xc - 154, xc + 154, yc - 136, yc + 125, 1, 0, 1, 0, 1);
        builder.end();

        GLUtil.enableBlend();
        STUDIO_LOGO.bind();
        builder.uploadPointer();
        STUDIO_LOGO.unbind();
        GLUtil.disableBlend();

        builder.free();
    }

    public boolean isAnimationCompleted() {
        return this.time > 350;
    }
}
