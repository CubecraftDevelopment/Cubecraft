package ink.flybird.cubecraft.client.gui;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializer;
import ink.flybird.cubecraft.client.ClientRenderContext;
import ink.flybird.cubecraft.client.ClientSharedContext;
import ink.flybird.cubecraft.client.CubecraftClient;
import ink.flybird.cubecraft.client.gui.base.DisplayScreenInfo;
import ink.flybird.cubecraft.client.gui.base.Popup;
import ink.flybird.cubecraft.client.gui.base.Text;
import ink.flybird.cubecraft.client.gui.layout.Layout;
import ink.flybird.cubecraft.client.gui.node.Node;
import ink.flybird.cubecraft.client.gui.screen.Screen;
import ink.flybird.cubecraft.client.internal.registry.ClientSettingRegistry;
import ink.flybird.cubecraft.client.resources.ResourceLocation;
import ink.flybird.cubecraft.client.render.renderer.IComponentPartRenderer;
import ink.flybird.cubecraft.client.event.gui.ComponentInitializeEvent;
import ink.flybird.cubecraft.client.resources.item.ImageResource;
import ink.flybird.fcommon.event.SimpleEventBus;
import ink.flybird.quantum3d.device.Window;
import ink.flybird.quantum3d.device.event.MousePosEvent;
import ink.flybird.cubecraft.SharedContext;
import ink.flybird.quantum3d_legacy.GLUtil;
import ink.flybird.fcommon.container.CollectionUtil;
import ink.flybird.fcommon.event.EventBus;
import ink.flybird.fcommon.event.EventHandler;

import ink.flybird.fcommon.file.FAMLDeserializer;
import ink.flybird.fcommon.file.XmlReader;
import ink.flybird.fcommon.logging.Logger;
import ink.flybird.fcommon.logging.SimpleLogger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.*;


public final class GUIManager{
    private final Logger logger=new SimpleLogger("GUIManager");

    private final HashMap<String, Class<? extends Layout>> layoutClassMapping = new HashMap<>();


    private final HashMap<String, Class<? extends IComponentPartRenderer>> rendererPartClassMapping = new HashMap<>();
    private final HashMap<Class<? extends Node>, ComponentRenderer> renderers = new HashMap<>();
    private final HashMap<Class<? extends Node>, ResourceLocation> renderControllerLocations = new HashMap<>();

    private final XmlReader deserializer;
    private final CubecraftClient client;
    private final Window window;
    private final EventBus eventBus = new SimpleEventBus();
    private final boolean legacyFontRequest = false;
    private Screen screen;
    private Screen hoverScreen;
    private float alpha;
    private int animateStatus = 0;
    private boolean checked;
    private int fixedMouseX, fixedMouseY;

    public GUIManager(XmlReader reader, CubecraftClient client, Window window) {
        this.deserializer = reader;
        this.client = client;
        this.window = window;
        this.client.getDeviceEventBus().registerEventListener(this);
        //basic
        this.deserializer.registerDeserializer(Text.class, new Text.XMLDeserializer());
        this.deserializer.registerDeserializer(Layout.class, new Layout.XMLDeserializer());
        this.deserializer.registerDeserializer(Screen.class, new Screen.XMLDeserializer());
    }

    public void setScreen(String location, String parent) {
        try {
            if (location.endsWith(".xml") && parent.endsWith(".xml")) {
                Document dom = ClientSharedContext.RESOURCE_MANAGER.getResource(ResourceLocation.uiScreen(location)).getAsDom();
                Element faml = (Element) dom.getElementsByTagName("screen").item(0);
                Document dom2 = ClientSharedContext.RESOURCE_MANAGER.getResource(ResourceLocation.uiScreen(parent)).getAsDom();
                Element faml2 = (Element) dom2.getElementsByTagName("screen").item(0);
                Screen scr = this.deserializer.deserialize(faml, Screen.class);
                scr.setParentScreen(this.deserializer.deserialize(faml2, Screen.class));
                this.setScreen(scr);
            } else {
                throw new RuntimeException("loaded a none exist file!");
            }
        } catch (Exception e) {
            this.logger.exception(e);
            ScreenUtil.createPopup("GUI Exception", "failed to load screen.", 100, Popup.ERROR);
        }
    }

    public void broadCastInitializeEvent(Screen screen) {
        for (Node p : screen.getNodes().values()) {
            this.getEventBus().callEvent(new ComponentInitializeEvent(p, screen, this), screen.getID());
        }
    }

    public void registerLayout(String id, Class<? extends Layout> clazz, FAMLDeserializer<?> deserializer) {
        this.deserializer.registerDeserializer(clazz, deserializer);
        this.layoutClassMapping.put(id, clazz);
    }

    public void registerRenderController(Class<? extends Node> clazz, ResourceLocation loc) {
        this.renderControllerLocations.put(clazz, loc);
    }

    public void registerRendererPart(String id, Class<? extends IComponentPartRenderer> clazz, JsonDeserializer<?> deserializer) {
        this.rendererPartClassMapping.put(id, clazz);
        SharedContext.GSON_BUILDER.registerTypeAdapter(clazz, deserializer);
    }

    public Class<?> getLayoutClass(String type) {
        return this.layoutClassMapping.get(type);
    }

    public Class<? extends IComponentPartRenderer> getRendererPartClass(String id) {
        return this.rendererPartClassMapping.get(id);
    }

    public ComponentRenderer getRenderController(Class<? extends Node> clazz) {
        return this.renderers.get(clazz);
    }

    public void initialize() {
        SharedContext.GSON_BUILDER.registerTypeAdapter(ComponentRenderer.class, new ComponentRenderer.JDeserializer());
        SharedContext.GSON_BUILDER.registerTypeAdapter(IComponentPartRenderer.class, new IComponentPartRenderer.JDeserializer());
        Gson gson = SharedContext.createJsonReader();
        CollectionUtil.iterateMap(this.renderControllerLocations, (key, item) -> this.renderers.put(key, gson.fromJson(
                ClientSharedContext.RESOURCE_MANAGER.getResource(item).getAsText(), ComponentRenderer.class)
        ));
        Set<ImageResource> locations = new HashSet<>();
        CollectionUtil.iterateMap(this.renderers, ((key, item) -> item.initializeModel(locations)));
        for (ImageResource resource : locations) {
            //todo:delegate resource
            ClientSharedContext.RESOURCE_MANAGER.loadResource(resource);
            ClientRenderContext.TEXTURE.createTexture2D(resource, false, false);
        }
    }

    public boolean isLegacyFontRequested() {
        return this.legacyFontRequest;
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
        String s = screen.getID();
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

    public EventBus getDeviceEventBus() {
        return this.client.getDeviceEventBus();
    }
}