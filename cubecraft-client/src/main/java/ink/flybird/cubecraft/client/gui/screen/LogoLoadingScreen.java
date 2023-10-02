package ink.flybird.cubecraft.client.gui.screen;

import ink.flybird.cubecraft.client.gui.ScreenUtil;
import ink.flybird.cubecraft.client.gui.base.DisplayScreenInfo;
import ink.flybird.cubecraft.client.gui.font.FontAlignment;
import ink.flybird.cubecraft.client.registry.ResourceRegistry;
import ink.flybird.fcommon.JVMInfo;
import ink.flybird.quantum3d_legacy.ShapeRenderer;
import ink.flybird.quantum3d_legacy.draw.VertexBuilder;
import ink.flybird.quantum3d_legacy.draw.VertexBuilderAllocator;
import ink.flybird.quantum3d_legacy.textures.Texture2D;

public final class LogoLoadingScreen extends AnimationScreen {
    private static final Texture2D GAME_LOGO = new Texture2D(false, false);

    private float progress = 1.0f;
    private String text;

    public LogoLoadingScreen() {
        super(false, "cubecraft:logo_loading", ScreenBackgroundType.IMAGE_BACKGROUND);
    }

    @Override
    public void init() {
        super.init();
        GAME_LOGO.load(ResourceRegistry.GAME_LOGO);
    }

    @Override
    public void render(DisplayScreenInfo info, float deltaTime) {
        super.render(info, deltaTime);

        int xc = info.centerX();
        int yc = info.centerY() - 25;

        VertexBuilder bg = VertexBuilderAllocator.createByPrefer(16);
        bg.begin();
        bg.color(33 / 255f, 33 / 255f, 33 / 255f, this.getContext().getHoverScreenAlpha());
        ShapeRenderer.drawRect(bg, 0, info.scrWidth(), 0, info.scrHeight(), 2, 2);
        bg.end();
        bg.uploadPointer();

        VertexBuilder builder = VertexBuilderAllocator.createByPrefer(64);
        builder.begin();
        Runtime runtime = Runtime.getRuntime();

        float prog = Math.min(this.time / 100f, this.progress);

        this.drawProgressBar(builder, xc, 20, 130, 1 - runtime.freeMemory() / (float) runtime.totalMemory());
        ScreenUtil.drawFontASCII("Mem-%s/%s-%s".formatted(JVMInfo.getUsedMemory(), JVMInfo.getTotalMemory(), JVMInfo.getUsage()), xc, 8, 16777215, 8, FontAlignment.MIDDLE);
        ScreenUtil.drawFontASCII("Loading-" + text + "(" + (int) (prog * 100) + "%)", xc - 250, yc + 65, 16777215, 8, FontAlignment.LEFT);

        this.drawProgressBar(builder, xc, yc + 80, 365, prog);
        builder.end();
        builder.uploadPointer();
        builder.free();
    }

    private void drawProgressBar(VertexBuilder builder, int xc, int yc, int w, float prog) {
        float a = this.getContext().getHoverScreenAlpha();
        builder.color(1, 1, 1, a);
        ShapeRenderer.drawRect(builder, xc - w / 2f, xc + w / 2f, yc - 6, yc + 6, 0, 0);
        builder.color(0x33 / 255f, 0x33 / 255f, 0x33 / 255f, a);
        ShapeRenderer.drawRect(builder, xc - w / 2f + 1, xc + w / 2f - 1, yc - 6 + 1, yc + 6 - 1, 1, 1);
        builder.color(1, 1, 1, a);
        ShapeRenderer.drawRect(builder, xc - w / 2f + 3, xc - w / 2f + 3 + (w - 6) * prog, yc - 6 + 3, yc + 6 - 3, 0, 0);
    }

    @Override
    public boolean isAnimationNotCompleted() {
        return this.time <= 100;
    }

    public void updateProgress(float prog) {
        this.progress = prog;
    }

    public void setText(String newStage) {
        this.text = newStage;
    }
}
