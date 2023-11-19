package net.cubecraft.client.gui;

import net.cubecraft.client.ClientRenderContext;
import net.cubecraft.client.ClientSharedContext;
import net.cubecraft.client.CubecraftClient;
import net.cubecraft.client.gui.base.DisplayScreenInfo;
import net.cubecraft.client.gui.base.Popup;
import net.cubecraft.client.gui.base.Text;
import net.cubecraft.client.gui.layout.Layout;
import net.cubecraft.client.gui.node.Node;
import net.cubecraft.client.gui.screen.Screen;
import net.cubecraft.client.registry.ClientSettingRegistry;
import net.cubecraft.client.render.renderer.ComponentRendererPart;
import net.cubecraft.client.resource.TextureAsset;
import net.cubecraft.client.resource.UIAsset;
import net.cubecraft.event.resource.ResourceLoadStartEvent;
import net.cubecraft.resource.ResourceLocation;
import ink.flybird.fcommon.container.CollectionUtil;
import ink.flybird.fcommon.event.EventBus;
import ink.flybird.fcommon.event.EventHandler;
import ink.flybird.fcommon.event.SimpleEventBus;
import ink.flybird.fcommon.file.XmlReader;
import ink.flybird.jflogger.ILogger;
import ink.flybird.jflogger.LogManager;
import ink.flybird.quantum3d.device.Window;
import ink.flybird.quantum3d.device.event.MousePosEvent;
import ink.flybird.quantum3d_legacy.GLUtil;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.HashSet;
import java.util.Set;


public final class GUIContext {
    private static final ILogger LOGGER = LogManager.getLogger("gui-context");

    private final XmlReader deserializer;
    private final CubecraftClient client;
    private final Window window;
    private final EventBus eventBus = new SimpleEventBus();
    private Screen screen;
    private Screen hoverScreen;
    private float alpha;
    private int animateStatus = 0;
    private boolean checked;
    private int fixedMouseX, fixedMouseY;

    public GUIContext(XmlReader reader, CubecraftClient client, Window window) {
        this.deserializer = reader;
        this.client = client;
        this.window = window;
        this.client.getDeviceEventBus().registerEventListener(this);
        //basic
        this.deserializer.registerDeserializer(Text.class, new Text.XMLDeserializer());
        this.deserializer.registerDeserializer(Layout.class, new Layout.XMLDeserializer());
        ClientSharedContext.RESOURCE_MANAGER.getEventBus().registerEventListener(this);
    }

    public void setScreen(UIAsset screen, @Nullable UIAsset parent) {
        Screen scr = new Screen(screen.getDom().getDocumentElement());
        Screen parentScr = parent != null ? new Screen(parent.getDom().getDocumentElement()) : null;

        scr.setParentScreen(parentScr);

        this.setScreen(scr);
    }

    public void setScreen(String location, String parent) {
        try {
            if (location.endsWith(".xml") && parent.endsWith(".xml")) {
            } else {
                throw new RuntimeException("loaded a none exist file!");
            }
        } catch (Exception e) {
            LOGGER.error(e);
            ScreenUtil.createPopup("GUI Exception", "failed to load screen.", 100, Popup.ERROR);
        }
    }


    public void broadCastInitializeEvent(Screen screen) {
        for (Node p : screen.getNodes().values()) {

        }
    }

    public static Class<?> getLayoutClass(String type) {
        return GUIRegistry.layoutClassMapping.get(type);
    }

    public Class<? extends ComponentRendererPart> getRendererPartClass(String id) {
        return GUIRegistry.COMPONENT_RENDERER_PART.get(id);
    }

    public ComponentRenderer getRenderController(Class<? extends Node> clazz) {
        return GUIRegistry.COMPONENT_RENDERER.get(clazz);
    }

    public void initialize() {
        Set<TextureAsset> locations = new HashSet<>();
        CollectionUtil.iterateMap(GUIRegistry.COMPONENT_RENDERER, ((key, item) -> {
            if (item == null) {
                return;
            }
            item.initializeModel(locations);
        }));
        for (TextureAsset resource : locations) {
            //todo:delegate resource
            ClientSharedContext.RESOURCE_MANAGER.loadResource(resource);
            ClientRenderContext.TEXTURE.createTexture2D(resource, false, false);
        }
    }

    public void render(DisplayScreenInfo info, float interpolatedTime) {
        GLUtil.disableDepthTest();
        if (this.screen != null) {
            this.screen.render(info, interpolatedTime);
        }
        if (this.hoverScreen != null) {
            if (alpha > 0 && animateStatus == -1) {
                alpha -= 0.02F;
            }
            if (alpha < 0 && animateStatus == 1) {
                alpha += 0.02F;
            }
            if (alpha >= 1 && checked) {
                animateStatus = 0;
                checked = false;
            }
            if (alpha > 0) {
                this.hoverScreen.render(info, interpolatedTime);
            }
        }
        GLUtil.enableDepthTest();
        ScreenUtil.renderToasts(info, interpolatedTime);
    }

    public void tick() {
        if (this.screen != null) {
            this.screen.tick();
        }
        if (this.hoverScreen != null && alpha > 0) {
            this.hoverScreen.tick();
        }
    }

    public void swapLayer() {
        Screen screen = this.screen;
        this.screen = hoverScreen;
        this.hoverScreen = screen;
    }

    public void disposeHoverScreen() {
        alpha = 1;
        animateStatus = -1;
    }

    public void introHoverScreen() {
        alpha = 0;
        animateStatus = 1;
        checked = true;
    }

    public void displayHoverScreen() {
        alpha = 1;
        animateStatus = 0;
    }

    public float getHoverScreenAlpha() {
        return alpha;
    }

    public Screen getScreen() {
        return screen;
    }

    public void setScreen(UIAsset screen) {
        this.setScreen(screen, null);
    }

    public void setScreen(String location) {
        if (location.endsWith(".xml")) {
            Document dom = ClientSharedContext.RESOURCE_MANAGER.getResource(ResourceLocation.uiScreen(location)).getAsDom();
            this.setScreen((Screen) this.deserializer.deserialize(((Element) dom.getFirstChild()), Screen.class));
        } else {
            throw new RuntimeException("loaded a none exist file!");
        }
    }

    public void setScreen(Screen screen) {
        if (this.screen != null) {
            this.screen.release();
        }
        this.screen = screen;
        this.screen.init();
        this.broadCastInitializeEvent(this.screen);
    }

    public Screen getHoverScreen() {
        return hoverScreen;
    }

    public void setHoverScreen(String location) {
        if (location.endsWith(".xml")) {
            Document dom = ClientSharedContext.RESOURCE_MANAGER.getResource(ResourceLocation.uiScreen(location)).getAsDom();
            Element faml = (Element) dom.getElementsByTagName("faml").item(0);
            if (!faml.getAttribute("ext").equals("cubecraft_ui")) {
                throw new RuntimeException("invalid ui xml");
            }
            this.setHoverScreen((Screen) this.deserializer.deserialize(faml, Screen.class));
        } else {
            throw new RuntimeException("loaded a none exist file!");
        }
    }

    public void setHoverScreen(Screen screen) {
        if (this.hoverScreen != null) {
            this.hoverScreen.release();
        }
        this.hoverScreen = screen;
        this.hoverScreen.init();
        String s = screen.getId();
        this.broadCastInitializeEvent(this.hoverScreen);
    }

    public EventBus getEventBus() {
        return eventBus;
    }

    public Window getWindow() {
        return window;
    }

    public int getFixedMouseX() {
        return fixedMouseX;
    }

    public int getFixedMouseY() {
        return fixedMouseY;
    }

    @EventHandler
    public void onMousePos(MousePosEvent e) {
        double scale = ClientSettingRegistry.GUI_SCALE.getValue();
        this.fixedMouseX = (int) (e.getX() / scale);
        this.fixedMouseY = (int) (e.getY() / scale);
    }

    @EventHandler
    public void onResourceReload(ResourceLoadStartEvent event) {
        initialize();
    }

    public EventBus getDeviceEventBus() {
        return this.client.getDeviceEventBus();
    }
}