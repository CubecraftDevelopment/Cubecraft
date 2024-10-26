package net.cubecraft.client.gui.screen;

import ink.flybird.quantum3d_legacy.BufferAllocation;
import ink.flybird.quantum3d_legacy.GLUtil;
import ink.flybird.quantum3d_legacy.draw.VertexBuilderAllocator;
import ink.flybird.quantum3d_legacy.draw.VertexUploader;
import me.gb2022.commons.JVMInfo;
import me.gb2022.commons.container.OrderedHashMap;
import net.cubecraft.SharedObjects;
import net.cubecraft.client.ClientSettingRegistry;
import net.cubecraft.client.ClientSharedContext;
import net.cubecraft.client.CubecraftClient;
import net.cubecraft.client.context.ClientGUIContext;
import net.cubecraft.client.event.gui.component.ComponentInitializeEvent;
import net.cubecraft.client.gui.ScreenUtil;
import net.cubecraft.client.gui.base.DisplayScreenInfo;
import net.cubecraft.client.gui.font.FontAlignment;
import net.cubecraft.client.gui.node.Container;
import net.cubecraft.client.gui.node.Node;
import net.cubecraft.client.util.IMBlocker;
import net.cubecraft.text.TextComponent;
import net.cubecraft.util.SystemInfoQuery;
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
        this.context = ClientSharedContext.getClient().getClientGUIContext();
    }


    public Screen(Element element) {
        this(false, "_test", ScreenBackgroundType.EMPTY);
        this.init(element);
        this.setContext(this, this, ClientSharedContext.getClient().getClientGUIContext());
    }

    @Override
    public Node getParent() {
        return this;
    }

    @Override
    public void init(Element element) {
        this.backgroundType = ScreenBackgroundType.from(element.getAttribute("bg"));
        this.id = element.getAttribute("id");
        this.deserializeChild(element);
        IMBlocker.set(false);
    }

    public void init() {
        this.client = ClientSharedContext.getClient();
        this.client.getDeviceEventBus().registerEventListener(this);
        this.client.getClientDeviceContext().getMouse().setMouseGrabbed(this.grabMouse);
        this.context.getEventBus().callEvent(new ComponentInitializeEvent(this, this, this.context), getId());
    }

    //debug
    public void renderDebug(DisplayScreenInfo info) {
        int pos = 2;
        for (String s : this.debugInfoLeft.values()) {
            ClientGUIContext.FONT_RENDERER.renderShadow(s, 2, pos, 16777215, 8, FontAlignment.LEFT);
            pos += 10;
        }
        pos = 2;
        for (String s : this.debugInfoRight.values()) {
            ClientGUIContext.FONT_RENDERER.renderShadow(s, info.getScreenWidth() - 2, pos, 16777215, 8, FontAlignment.RIGHT);
            pos += 10;
        }
    }


    public void getDebug() {
        this.debugInfoLeft.put("version", "version: %s".formatted(
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
                getId(),
                this.parent == null ? null : this.parent.getId()
        ));
        VertexUploader.resetUploadCount();
        this.debugInfoRight.put("vm", "VM: %s-%s, os:%s-%s".formatted(
                JVMInfo.getJavaName(),
                JVMInfo.getJavaVersion(),
                JVMInfo.getOSName(),
                JVMInfo.getOSVersion()
        ));
        this.debugInfoRight.put("mem", "Mem: %s/%s(%s),o: %sMB(%d)".formatted(
                JVMInfo.getUsedMemory(),
                JVMInfo.getTotalMemory(),
                JVMInfo.getUsage(),
                SharedObjects.SHORT_DECIMAL_FORMAT.format(BufferAllocation.getAllocSize() / 1024f / 1024),
                BufferAllocation.getAllocInstances()
        ));
        this.debugInfoRight.put("cpu", "CPU: %s".formatted(SystemInfoQuery.getCPUInfo()));
        this.debugInfoRight.put("gpu", "GPU: %s".formatted(SystemInfoQuery.getGPUInfo()));
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


        if (ClientSharedContext.getClient().isDebug) {
            this.debugInfoLeft.clear();
            this.debugInfoRight.clear();
            this.getDebug();
            this.renderDebug(info);
        } else {
            TextComponent text=TextComponent.create("fps:").color(0xffff00).append(TextComponent.create(client.getFPS()).color(0xffff00));
            ClientGUIContext.FONT_RENDERER.render(text, info.getScreenWidth() - 2, 2, 0, FontAlignment.RIGHT);
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

    public void release() {
        this.destroy();
        Screen.this.client.getDeviceEventBus().unregisterEventListener(this);
    }

    public ScreenBackgroundType getBackgroundType() {
        return backgroundType;
    }

    @Override
    public String getId() {
        return this.id;
    }
}