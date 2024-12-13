package net.cubecraft.client.gui.screen;

import me.gb2022.commons.JVMInfo;
import me.gb2022.commons.container.OrderedHashMap;
import me.gb2022.quantum3d.legacy.draw.VertexBuilderAllocator;
import me.gb2022.quantum3d.memory.LWJGLSecureMemoryManager;
import me.gb2022.quantum3d.render.vertex.VertexBuilderUploader;
import me.gb2022.quantum3d.util.GLUtil;
import net.cubecraft.SharedObjects;
import net.cubecraft.client.ClientSharedContext;
import net.cubecraft.client.CubecraftClient;
import net.cubecraft.client.event.gui.component.ComponentInitializeEvent;
import net.cubecraft.client.gui.ScreenUtil;
import net.cubecraft.client.gui.base.DisplayScreenInfo;
import net.cubecraft.client.gui.font.FontAlignment;
import net.cubecraft.client.gui.font.FontRenderer;
import net.cubecraft.client.gui.node.Container;
import net.cubecraft.client.gui.node.Node;
import net.cubecraft.client.registry.ClientSettings;
import net.cubecraft.client.util.IMBlocker;
import net.cubecraft.util.SystemInfoQuery;
import org.w3c.dom.Element;

import java.util.HashSet;
import java.util.Set;

public class Screen extends Container {
    protected final Set<ScreenAttachment> attachments = new HashSet<>();
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
        super.init();
        this.client = ClientSharedContext.getClient();
        this.client.getClientDeviceContext().getMouse().setMouseGrabbed(this.grabMouse);
        this.context.getEventBus().callEvent(new ComponentInitializeEvent(this, this, this.context), getId());
    }

    //debug
    public void renderDebug(DisplayScreenInfo info) {
        int pos = 2;
        for (String s : this.debugInfoLeft.values()) {
            FontRenderer.ttf().renderShadow(s, 2, pos, 16777215, 8, FontAlignment.LEFT);
            pos += 10;
        }
        pos = 2;
        for (String s : this.debugInfoRight.values()) {
            FontRenderer.ttf().renderShadow(s, info.getScreenWidth() - 2, pos, 16777215, 8, FontAlignment.RIGHT);
            pos += 10;
        }
    }

    public void debugLeft(String id, String line, Object... format) {
        this.debugInfoLeft.put(id, line.formatted(format));
    }

    public void debugRight(String id, String line, Object... format) {
        this.debugInfoRight.put(id, line.formatted(format));
    }

    public void getDebug() {
        var r_fps = this.client.getFPS();
        var r_vac = VertexBuilderAllocator.ALLOCATED_COUNT.get();
        var r_vuc = VertexBuilderUploader.getUploadedCount();

        var vm_name = JVMInfo.getJavaName();
        var vm_ver = JVMInfo.getJavaVersion();
        var vm_osn = JVMInfo.getOSName();
        var vm_osv = JVMInfo.getOSVersion();

        var m_vmu = JVMInfo.getUsedMemory();
        var m_vmp = JVMInfo.getUsage();
        var m_vmt = JVMInfo.getTotalMemory();

        var m_oi = LWJGLSecureMemoryManager.INSTANCES.get();
        var m_oa = SharedObjects.SHORT_DECIMAL_FORMAT.format(LWJGLSecureMemoryManager.USAGE.get() / 1048576f);

        this.debugLeft("version", "version: %s".formatted(CubecraftClient.VERSION));
        this.debugLeft("fps", "fps: %d, builder: %d, vert: %d", r_fps, r_vac, r_vuc);
        this.debugLeft("tps", "tps: %d", this.client.getTPS());
        this.debugLeft("gui", "scr: %s(%s)".formatted(getId(), this.parent == null ? null : this.parent.getId()));
        this.debugRight("vm", "VM: %s-%s, os:%s-%s", vm_name, vm_ver, vm_osn, vm_osv);
        this.debugRight("mem", "Mem: %s/%s[%s] (o:%s=%sMB)", m_vmu, m_vmt, m_vmp, m_oi, m_oa);
        this.debugRight("cpu", "CPU: %s".formatted(SystemInfoQuery.getCPUInfo()));
        this.debugRight("gpu", "GPU: %s".formatted(SystemInfoQuery.getGPUInfo()));
    }

    //run
    public void render(DisplayScreenInfo info, float deltaTime, float alphaOverwrite) {
        for (var a : this.getAttachments()) {
            a.render(this, info, deltaTime, alphaOverwrite);
        }

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
            FontRenderer.ttf()
                    .renderShadow("fps: %s".formatted(client.getFPS()), info.getScreenWidth() - 2, 2, 0xffff00, 8, FontAlignment.RIGHT);
        }
    }

    public void tick() {
        super.tick();

        for (var a : this.getAttachments()) {
            a.tick();
        }

        double scale = ClientSettings.UISetting.getFixedGUIScale();
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
        for (var a : this.getAttachments()) {
            a.release();
        }

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

    //attachment
    public void addAttachment(ScreenAttachment attachment) {
        this.attachments.add(attachment);
    }

    public void removeAttachment(ScreenAttachment attachment) {
        this.attachments.remove(attachment);
    }

    public Set<ScreenAttachment> getAttachments() {
        return attachments;
    }
}