package net.cubecraft.client.gui.screen;

import me.gb2022.quantum3d.legacy.draw.LegacyVertexBuilder;
import me.gb2022.quantum3d.legacy.draw.VertexBuilderAllocator;
import net.cubecraft.client.gui.base.DisplayScreenInfo;
import net.cubecraft.client.registry.ResourceRegistry;
import me.gb2022.quantum3d.util.GLUtil;
import me.gb2022.quantum3d.util.ShapeRenderer;
import me.gb2022.quantum3d.texture.Texture2D;
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
        int xc = info.getCenterX();
        int yc = info.getCenterY() - 25;
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

        LegacyVertexBuilder builder = VertexBuilderAllocator.createByPrefer(32);

        builder.begin();
        builder.color((int) (255 * bgAlpha), (int) (178 * bgAlpha), (int) (26 * bgAlpha));
        ShapeRenderer.drawRect(builder, 0, info.getScreenWidth(), 0, info.getScreenHeight(), 2, 2);
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
