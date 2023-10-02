package ink.flybird.cubecraft.client.gui.screen;

import ink.flybird.cubecraft.client.gui.base.DisplayScreenInfo;
import ink.flybird.cubecraft.client.registry.ResourceRegistry;
import ink.flybird.quantum3d_legacy.GLUtil;
import ink.flybird.quantum3d_legacy.ShapeRenderer;
import ink.flybird.quantum3d_legacy.draw.VertexBuilder;
import ink.flybird.quantum3d_legacy.draw.VertexBuilderAllocator;
import ink.flybird.quantum3d_legacy.textures.Texture2D;
import org.lwjgl.opengl.GL11;

public class StudioLoadingScreen extends AnimationScreen {
    private static final Texture2D STUDIO_LOGO = new Texture2D(false, false);

    public StudioLoadingScreen() {
        super(false, "_", ScreenBackgroundType.EMPTY);
    }

    @Override
    public void init() {
        super.init();
        STUDIO_LOGO.load(ResourceRegistry.STUDIO_LOGO);
    }

    @Override
    public void render(DisplayScreenInfo info, float deltaTime) {
        super.render(info, deltaTime);
        GLUtil.enableBlend();
        int xc = info.centerX();
        int yc = info.centerY() - 25;
        float alpha = 0f;
        float bgAlpha = 1f;

        if (this.time < 75) {
            alpha = this.time / 75f;
        }
        if (this.time >= 75 && this.time < 225) {
            alpha = 1f;
        }
        if (this.time >= 225 && this.time <= 300) {
            alpha = 1 - (this.time - 225) / 75f;
        }
        if (this.time >= 300 && this.time <= 375) {
            bgAlpha = 1 - (this.time - 300) / 75f;
        }
        if (this.time >= 375) {
            bgAlpha = 0f;
        }

        VertexBuilder builder = VertexBuilderAllocator.createByPrefer(32);

        builder.begin();
        builder.color((int) (255 * bgAlpha), (int) (178 * bgAlpha), (int) (26 * bgAlpha));
        ShapeRenderer.drawRect(builder, 0, info.scrWidth(), 0, info.scrHeight(), 2, 2);
        builder.end();

        builder.uploadPointer();

        GL11.glClearColor(255 / 255f, 178 / 255f, 26 / 255f, 1);

        builder.begin();
        builder.color(1, 1, 1, alpha);

        int r=125;
        ShapeRenderer.drawRectUV(builder, xc - r, xc + r, yc - r, yc + r, 1, 0, 1, 0, 1);
        builder.end();


        STUDIO_LOGO.bind();
        builder.uploadPointer();
        STUDIO_LOGO.unbind();
        GLUtil.disableBlend();

        builder.free();
    }

    public boolean isAnimationNotCompleted() {
        return this.time <= 375;
    }
}
