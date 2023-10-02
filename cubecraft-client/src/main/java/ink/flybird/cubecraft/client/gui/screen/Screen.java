package ink.flybird.cubecraft.client.gui.screen;

import ink.flybird.cubecraft.client.ClientSharedContext;
import ink.flybird.cubecraft.client.CubecraftClient;
import ink.flybird.cubecraft.client.gui.base.DisplayScreenInfo;
import ink.flybird.cubecraft.client.gui.font.FontAlignment;
import ink.flybird.cubecraft.client.gui.node.Container;
import ink.flybird.cubecraft.client.registry.ClientSettingRegistry;
import ink.flybird.fcommon.JVMInfo;
import ink.flybird.fcommon.container.OrderedHashMap;
import ink.flybird.fcommon.file.FAMLDeserializer;
import ink.flybird.fcommon.file.XmlReader;
import ink.flybird.cubecraft.client.gui.ScreenUtil;
import ink.flybird.quantum3d_legacy.BufferAllocation;
import ink.flybird.quantum3d_legacy.GLUtil;
import ink.flybird.quantum3d_legacy.draw.VertexBuilderAllocator;
import ink.flybird.quantum3d_legacy.draw.VertexUploader;
import org.w3c.dom.Element;

public class Screen extends Container {
    protected final OrderedHashMap<String, String> debugInfoLeft = new OrderedHashMap<>();
    protected final OrderedHashMap<String, String> debugInfoRight = new OrderedHashMap<>();
    protected final boolean grabMouse;
    public String id;
    public ScreenBackgroundType backgroundType;
    protected CubecraftClient client;
    private Screen parent;

    //init
    public Screen(boolean grabMouse, String id, ScreenBackgroundType type) {
        this.grabMouse = grabMouse;
        this.id = id;
        this.backgroundType = type;
        this.context = CubecraftClient.CLIENT.getGuiManager();
    }

    @Override
    public void init(Element element) {
        this.backgroundType = ScreenBackgroundType.from(element.getAttribute("bg"));
        this.id = element.getAttribute("id");
        this.deserializeChild(element);
    }

    public void init() {
        this.client = CubecraftClient.CLIENT;
        this.client.getDeviceEventBus().registerEventListener(this);
        this.client.getMouse().setMouseGrabbed(this.grabMouse);
    }

    //debug
    public void renderDebug(DisplayScreenInfo info) {
        int pos = 2;
        for (String s : this.debugInfoLeft.values()) {
            ClientSharedContext.SMOOTH_FONT_RENDERER.renderShadow(s, 2, pos, 16777215, 8, FontAlignment.LEFT);
            pos += 10;
        }
        pos = 2;
        for (String s : this.debugInfoRight.values()) {
            ClientSharedContext.SMOOTH_FONT_RENDERER.renderShadow(s, info.scrWidth() - 2, pos, 16777215, 8, FontAlignment.RIGHT);
            pos += 10;
        }
    }


    public void getDebug() {
        CubecraftClient client = CubecraftClient.CLIENT;
        this.debugInfoLeft.put("version", "version: client - %s".formatted(
                CubecraftClient.VERSION
        ));
        this.debugInfoLeft.put("fps", "fps: %d, builder: %d, vert: %d".formatted(
                this.client.getFPS(),
                VertexBuilderAllocator.ALLOCATED_COUNT.get(),
                VertexUploader.getUploadedCount()
        ));
        this.debugInfoLeft.put("tps", "tps: %d".formatted(
                this.client.getTPS()
        ));
        this.debugInfoLeft.put("gui", "scr: %s(%s)".formatted(
                this.getID(),
                this.parent == null ? null : this.parent.getID()
        ));
        VertexUploader.resetUploadCount();

        this.debugInfoRight.put("platform", "platform: %s/%s, os:%s/%s".formatted(
                JVMInfo.getJavaName(),
                JVMInfo.getJavaVersion(),
                JVMInfo.getOSName(),
                JVMInfo.getOSVersion()
        ));
        this.debugInfoRight.put("mem", "mem: %s/%s(%s),o: %dMB(%d)".formatted(
                JVMInfo.getUsedMemory(),
                JVMInfo.getTotalMemory(),
                JVMInfo.getUsage(),
                BufferAllocation.getAllocSize() / 1024 / 1024,
                BufferAllocation.getAllocInstances()
        ));
    }

    //run
    public void render(DisplayScreenInfo info, float deltaTime) {
        switch (this.backgroundType) {
            case IMAGE_BACKGROUND -> ScreenUtil.renderPictureBackground(this.client.getWindow());
            case TILE_BACKGROUND -> ScreenUtil.renderTileBackground();
            case IN_GAME -> {
            }
            case IMAGE_BLUR_BACKGROUND -> ScreenUtil.renderPictureBackgroundBlur(this.client.getWindow());
            case IMAGE_BLUR_MASK_BACKGROUND -> {
                GLUtil.enableBlend();
                ScreenUtil.renderPictureBackgroundBlur(this.client.getWindow());
                ScreenUtil.renderMask(this.client.getWindow());
            }
            case IN_GAME_MASK -> ScreenUtil.renderMask(this.client.getWindow());
        }
        super.render(deltaTime);
        if (CubecraftClient.CLIENT.isDebug) {
            this.debugInfoLeft.clear();
            this.debugInfoRight.clear();
            this.getDebug();
            this.renderDebug(info);
        }
    }

    public void tick() {
        super.tick();
        double scale = ClientSettingRegistry.GUI_SCALE.getValue();
        this.onResize(0, 0, (int) (this.context.getWindow().getWidth() / scale), (int) (this.context.getWindow().getHeight() / scale));
    }


    //attribute

    public Screen getParentScreen() {
        return this.parent;
    }

    public void setParentScreen(Screen scr) {
        this.parent = scr;
    }

    public CubecraftClient getClient() {
        return client;
    }

    public String getID() {
        return id;
    }

    public void release() {
        this.destroy();
        Screen.this.client.getDeviceEventBus().unregisterEventListener(this);
    }

    public ScreenBackgroundType getBackgroundType() {
        return backgroundType;
    }

    //decode
    public static class XMLDeserializer implements FAMLDeserializer<Screen> {
        @Override
        public Screen deserialize(Element element, XmlReader reader) {
            Screen screen = new Screen(false, "_test", ScreenBackgroundType.EMPTY);
            screen.init(element);
            screen.setContext(screen, null, CubecraftClient.CLIENT.getGuiManager());
            return screen;
        }
    }
}