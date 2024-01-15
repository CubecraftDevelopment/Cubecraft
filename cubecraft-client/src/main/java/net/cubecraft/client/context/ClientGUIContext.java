package net.cubecraft.client.context;

import com.google.gson.JsonDeserializer;
import ink.flybird.fcommon.container.CollectionUtil;
import ink.flybird.fcommon.event.EventBus;
import ink.flybird.fcommon.event.EventHandler;
import ink.flybird.fcommon.event.SimpleEventBus;
import ink.flybird.fcommon.file.XmlReader;
import ink.flybird.fcommon.registry.ConstructingMap;
import ink.flybird.fcommon.registry.TypeItem;
import ink.flybird.fcommon.threading.TaskProgressUpdateListener;
import ink.flybird.jflogger.ILogger;
import ink.flybird.jflogger.LogManager;
import ink.flybird.quantum3d_legacy.GLUtil;
import me.gb2022.quantum3d.device.Window;
import me.gb2022.quantum3d.device.event.MousePosEvent;
import net.cubecraft.SharedContext;
import net.cubecraft.client.ClientSettingRegistry;
import net.cubecraft.client.ClientSharedContext;
import net.cubecraft.client.CubecraftClient;
import net.cubecraft.client.gui.ComponentRenderer;
import net.cubecraft.client.gui.ScreenUtil;
import net.cubecraft.client.gui.base.DisplayScreenInfo;
import net.cubecraft.client.gui.base.Popup;
import net.cubecraft.client.gui.base.Text;
import net.cubecraft.client.gui.font.SmoothedFontRenderer;
import net.cubecraft.client.gui.layout.Border;
import net.cubecraft.client.gui.layout.Layout;
import net.cubecraft.client.gui.node.Node;
import net.cubecraft.client.gui.screen.AnimationScreen;
import net.cubecraft.client.gui.screen.HUDScreen;
import net.cubecraft.client.gui.screen.LogoLoadingScreen;
import net.cubecraft.client.gui.screen.Screen;
import net.cubecraft.client.render.renderer.ComponentRendererPart;
import net.cubecraft.client.resource.ModelAsset;
import net.cubecraft.client.resource.TextureAsset;
import net.cubecraft.client.resource.UIAsset;
import net.cubecraft.event.resource.ResourceLoadStartEvent;
import net.cubecraft.resource.ResourceLocation;
import net.cubecraft.world.IWorld;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;


public final class ClientGUIContext extends ClientContext implements TaskProgressUpdateListener {
    public static final ConstructingMap<Node> NODE = new ConstructingMap<>(Node.class);
    public static final ConstructingMap<Layout> LAYOUT = new ConstructingMap<>(Layout.class);
    public static final HashMap<Class<? extends Node>, ComponentRenderer> COMPONENT_RENDERER = new HashMap<>();
    public static final HashMap<String, Class<? extends ComponentRendererPart>> COMPONENT_RENDERER_PART = new HashMap<>();
    public static final SmoothedFontRenderer SMOOTH_FONT_RENDERER = new SmoothedFontRenderer();
    public static final SmoothedFontRenderer ICON_FONT_RENDERER = new SmoothedFontRenderer();
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

    private LogoLoadingScreen loadingScreen;

    public ClientGUIContext(CubecraftClient client, Window window) {
        super(client);
        this.deserializer = SharedContext.FAML_READER;
        this.client = client;
        this.window = window;
        this.client.getDeviceEventBus().registerEventListener(this);
        this.deserializer.registerDeserializer(Text.class, new Text.XMLDeserializer());
        this.deserializer.registerDeserializer(Layout.class, new Layout.XMLDeserializer());
        ClientSharedContext.RESOURCE_MANAGER.getEventBus().registerEventListener(this);
    }


    public static void registerComponent(Class<? extends Node> clazz) {
        String id = clazz.getDeclaredAnnotation(TypeItem.class).value();
        NODE.registerItem(id, clazz);
        ModelAsset asset = new ModelAsset("cubecraft:/ui/" + id + ".json");
        String resID = "cubecraft:" + id + "_render_controller";
        ClientSharedContext.RESOURCE_MANAGER.registerResource("default", resID, asset);
        ClientSharedContext.RESOURCE_MANAGER.loadResource(asset);
        ComponentRenderer renderer = SharedContext.createJsonReader().fromJson(asset.getRawText(), ComponentRenderer.class);
        COMPONENT_RENDERER.put(clazz, renderer);
    }

    public static void registerRendererComponentPart(Class<? extends ComponentRendererPart> clazz, JsonDeserializer<?> deserializer) {
        String id = clazz.getDeclaredAnnotation(TypeItem.class).value();
        COMPONENT_RENDERER_PART.put(id, clazz);
        SharedContext.GSON_BUILDER.registerTypeAdapter(clazz, deserializer);
    }

    public static Layout createLayout(String content, String border) {
        String type = content.split("/")[0];
        String cont = content.split("/")[1];

        Layout layout = LAYOUT.create(type);

        layout.initialize(cont.split(","));

        if (Objects.equals(border, "")) {
            return layout;
        }

        layout.setBorder(new Border(
                Integer.parseInt(border.split(",")[0]),
                Integer.parseInt(border.split(",")[1]),
                Integer.parseInt(border.split(",")[3]),
                Integer.parseInt(border.split(",")[2])
        ));
        return layout;
    }

    public static Node createNode(String type, Element element) {
        Node n = NODE.create(type);
        n.init(element);

        return n;
    }

    public static void initializeContext() {
        SharedContext.GSON_BUILDER.registerTypeAdapter(ComponentRenderer.class, new ComponentRenderer.JDeserializer());
        SharedContext.GSON_BUILDER.registerTypeAdapter(ComponentRendererPart.class, new ComponentRendererPart.JDeserializer());
    }

    public static Class<? extends ComponentRendererPart> getRendererPartClass(String id) {
        return COMPONENT_RENDERER_PART.get(id);
    }

    public static ComponentRenderer getRenderController(Class<? extends Node> aClass) {
        return COMPONENT_RENDERER.get(aClass);
    }

    public static Class<?> getLayoutClass(String type) {
        return NODE.getMap().get(type);
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

    public void initialize() {
        Set<TextureAsset> locations = new HashSet<>();
        CollectionUtil.iterateMap(COMPONENT_RENDERER, ((key, item) -> {
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

    public void setHoverScreen(Screen screen) {
        if (this.hoverScreen != null) {
            this.hoverScreen.release();
        }
        this.hoverScreen = screen;
        this.hoverScreen.init();
        String s = screen.getId();
        this.broadCastInitializeEvent(this.hoverScreen);
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
        if(Objects.equals(event.getStage(), "client:startup")){
            this.loadingScreen = new LogoLoadingScreen();
        }
    }

    public EventBus getDeviceEventBus() {
        return this.client.getDeviceEventBus();
    }

    @Override
    public void joinWorld(IWorld world) {
        this.setScreen(new HUDScreen());
    }

    public void setHoverLoadingScreen() {
        this.setHoverScreen(this.loadingScreen);
    }

    public void renderAnimationLoadingScreen() {
        this.renderAnimationScreen(this.loadingScreen);
    }

    public void renderAnimationScreen(AnimationScreen screen) {
        while (screen.isAnimationNotCompleted()) {
            this.client.render();
            this.getWindow().update();
            Thread.yield();
        }
    }

    public LogoLoadingScreen getLoadingScreen() {
        return this.loadingScreen;
    }


    @Override
    public void onProgressChange(int progress) {
        this.loadingScreen.updateProgress(progress / 100f);
    }

    @Override
    public void onProgressStageChanged(String newStage) {
        this.loadingScreen.setText(newStage);
    }

    @Override
    public void refreshScreen() {
        this.client.shortTick();
        if (System.currentTimeMillis() % 5 == 0) {
            this.tick();
        }
    }
}