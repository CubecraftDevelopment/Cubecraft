package ink.flybird.cubecraft.client.internal.gui.screen;

import ink.flybird.cubecraft.client.gui.base.DisplayScreenInfo;
import ink.flybird.cubecraft.client.gui.screen.Screen;
import ink.flybird.cubecraft.client.gui.screen.ScreenBackgroundType;
import ink.flybird.quantum3d_legacy.textures.Texture2D;
import ink.flybird.cubecraft.client.internal.registry.ResourceRegistry;
import org.lwjgl.opengl.GL11;

public final class LogoLoadingScreen extends Screen {
    private static final Texture2D STUDIO_LOGO = new Texture2D(false, false);
    private static final Texture2D GAME_LOGO = new Texture2D(false, false);

    private long time;
    private float prog;
    private String text;
    private boolean animationCompleted = false;


    public LogoLoadingScreen() {
        super(false, "cubecraft:logo_loading", ScreenBackgroundType.IMAGE_BACKGROUND);
    }

    @Override
    public void init() {
        GAME_LOGO.load(ResourceRegistry.GAME_LOGO);
        STUDIO_LOGO.load(ResourceRegistry.STUDIO_LOGO);
    }



    @Override
    public void render(DisplayScreenInfo info, float interpolationTime) {
        GL11.glPushMatrix();
        GL11.glTranslatef(0, 0, 20);
        //shortTick logo




        /*
        builder = VertexBuilderAllocator.createByPrefer(256);
        builder.begin();
        Runtime runtime = Runtime.getRuntime();
        this.drawProgressBar(builder, xc, 20, 130, 1 - runtime.freeMemory() / (float) runtime.totalMemory());
        ScreenUtil.drawFontASCII("Mem-%s/%s-%s".formatted(JVMInfo.getUsedMemory(), JVMInfo.getTotalMemory(), JVMInfo.getUsage()), xc, 8, 16777215, 8, FontAlignment.MIDDLE);
        ScreenUtil.drawFontASCII("Loading-" + text + "(" + (int) (prog * 100) + "%)", xc - 250, yc + 65, 16777215, 8, FontAlignment.LEFT);


        this.drawProgressBar(builder, xc, yc + 80, 250, this.prog);
        ShapeRenderer.drawRect(builder, 0, info.scrWidth(), 0, info.scrHeight(), -1, -1);
        builder.end();
        builder.uploadPointer();
        builder.free();
         */
        GL11.glPopMatrix();
    }

    public void updateProgress(float prog) {
        this.prog = prog;
    }

    public void setText(String newStage) {
        this.text = newStage;
    }
}
