package net.cubecraft.client;

import me.gb2022.commons.event.SimpleEventBus;
import me.gb2022.commons.timer.Timer;
import me.gb2022.quantum3d.GameApplication;
import me.gb2022.quantum3d.device.DeviceContext;
import me.gb2022.quantum3d.device.Window;
import me.gb2022.quantum3d.device.adapter.WindowEventAdapter;
import me.gb2022.quantum3d.legacy.draw.VertexBuilderAllocator;
import me.gb2022.quantum3d.render.RenderContext;
import me.gb2022.quantum3d.util.GLContextManager;
import me.gb2022.quantum3d.util.GLUtil;
import me.gb2022.quantum3d.util.Sync;
import net.cubecraft.EnvironmentPath;
import net.cubecraft.SharedContext;
import net.cubecraft.Side;
import net.cubecraft.auth.Session;
import net.cubecraft.client.context.ClientGUIContext;
import net.cubecraft.client.event.ClientDisposeEvent;
import net.cubecraft.client.event.ClientPostSetupEvent;
import net.cubecraft.client.event.ClientSetupEvent;
import net.cubecraft.client.gui.GUIContext;
import net.cubecraft.client.gui.ScreenUtil;
import net.cubecraft.client.gui.base.DisplayScreenInfo;
import net.cubecraft.client.gui.font.FontRenderer;
import net.cubecraft.client.gui.screen.HUDScreen;
import net.cubecraft.client.gui.screen.Screen;
import net.cubecraft.client.gui.screen.ScreenBuilder;
import net.cubecraft.client.gui.screen.animation.StudioLoadingScreen;
import net.cubecraft.client.internal.PlayerController;
import net.cubecraft.client.internal.plugins.ScreenControllerBinder;
import net.cubecraft.client.net.base.NetworkClient;
import net.cubecraft.client.net.kcp.KCPNetworkClient;
import net.cubecraft.client.particle.ParticleEngine;
import net.cubecraft.client.registry.ClientSettings;
import net.cubecraft.client.registry.ResourceRegistry;
import net.cubecraft.client.registry.TextureRegistry;
import net.cubecraft.client.render.LevelRenderer;
import net.cubecraft.client.world.ActiveLevelProvider;
import net.cubecraft.client.world.LocalLevelProvider;
import net.cubecraft.mod.ModLoader;
import net.cubecraft.mod.ModManager;
import net.cubecraft.util.ObjectContainer;
import net.cubecraft.util.VersionInfo;
import net.cubecraft.util.setting.GameSetting;
import net.cubecraft.world.WorldContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class CubecraftClient extends GameApplication {
    public static final ObjectContainer<CubecraftClient> INSTANCE = new ObjectContainer<>(null);
    public static final VersionInfo VERSION = new VersionInfo("client-0.5.3");
    private static final Logger LOGGER = LogManager.getLogger("Client");

    private final List<ClientComponent> components = new ArrayList<>(4);
    private final DeviceHolder deviceContext = new DeviceHolder();
    private final GUIContext gui = new GUIContext();
    private final ClientGUIContext guiContext;

    private final SimpleEventBus clientEventBus = new SimpleEventBus();

    private final NetworkClient clientIO = new KCPNetworkClient();
    private final Session session = new Session("GrassBlock2022", "cubecraft:default");
    private final GameSetting setting = new GameSetting("cubecraft-client");
    public int maxFPS = 60;
    public boolean isDebug;
    private ActiveLevelProvider activeLevelProvider = null;
    private PlayerController controller;
    private ParticleEngine particleEngine;
    private long lastGCTime;
    private WorldContext worldContext;


    public CubecraftClient(DeviceContext deviceContext, RenderContext renderContext, Timer timer) {
        super(deviceContext, renderContext, timer);
        INSTANCE.setObj(this);
        deviceContext.initContext();

        //inject components here...
        this.components.add(new DeviceHolder());
        this.components.add(new LocalLevelProvider());
        this.components.add(new LevelRenderer());
        this.components.add(new ScreenControllerBinder());

        for (var component : this.components) {
            component.init(this);
        }

        this.guiContext = new ClientGUIContext(this, this.getWindow());
    }

    public static CubecraftClient getInstance() {
        return INSTANCE.getObj();
    }

    @Override
    public void initDevice(Window window) {
        this.setting.load();
        ClientSettings.register(this.setting);
        LOGGER.info("configuration loaded.");

        this.maxFPS = ClientSettings.RenderSetting.MAX_FPS.getValue();

        GLContextManager.createLegacyGLContext();
        GLContextManager.setGLContextVersion(4, 6);
        VertexBuilderAllocator.PREFER_MODE.set(0);
        LOGGER.info("render context started.");

        ClientContext.RESOURCE_MANAGER.getEventBus().registerEventListener(TextureRegistry.class);
        ClientContext.RESOURCE_MANAGER.getEventBus().registerEventListener(ResourceRegistry.class);
        ClientContext.RESOURCE_MANAGER.registerResources(ResourceRegistry.class);

        ClientContext.RESOURCE_MANAGER.load("client:startup", true);
        LOGGER.info("pre-load resources loaded.");

        for (var component : this.components) {
            component.deviceSetup(this, window, this.getDeviceContext());
        }

        window.setTitle("Cubecraft-" + CubecraftClient.VERSION.shortVersion());
        window.setSize(1280, 720);
        window.setFullscreen(ClientSettings.RenderSetting.FULL_SCREEN.getValue());
        window.setResizeable(true);
        window.setVsync(ClientSettings.RenderSetting.VSYNC.getValue());
        window.setIcon(ResourceRegistry.GAME_ICON.getStream());
        window.addListener(new WindowEventAdapter(this.getClientEventBus()));

        GLFW.glfwWindowHint(GLFW.GLFW_SCALE_TO_MONITOR, GLFW.GLFW_TRUE);

        LOGGER.info("window initialized.");
    }

    @Override
    public void initialize() {
        this.timer = new Timer(20);

        ModManager modManager = SharedContext.MOD;
        ModLoader.loadClientInternalMod();
        ModLoader.loadStandaloneMods(EnvironmentPath.getModFolder());
        modManager.completeModRegister(Side.CLIENT);
        modManager.constructMods(null);
        LOGGER.info("mods initialized.");

        this.guiContext.init();

        if (!ClientSettings.UISetting.SKIP_STUDIO_LOGO.getValue()) {
            StudioLoadingScreen scr = new StudioLoadingScreen();
            this.guiContext.setScreen(scr);
            this.guiContext.renderAnimationScreen(scr);
        }

        this.guiContext.setHoverLoadingScreen();
        this.guiContext.displayHoverScreen();

        long last = System.currentTimeMillis();
        modManager.getModLoaderEventBus().callEvent(new ClientSetupEvent(this));

        LOGGER.info("ui initialized.");

        modManager.getModLoaderEventBus().callEvent(new ClientPostSetupEvent(this));

        ClientContext.RESOURCE_MANAGER.load("default", true);
        ClientContext.RESOURCE_MANAGER.load("client:default", true);
        LOGGER.info("resource loaded.");

        this.guiContext.renderAnimationLoadingScreen();

        LOGGER.info("done, {}ms", System.currentTimeMillis() - last);

        this.guiContext.setScreen(ScreenBuilder.xml(ResourceRegistry.TITLE_SCREEN));
        this.guiContext.disposeHoverScreen();

        for (var component : this.components) {
            component.clientSetup(this);
        }
    }

    @Override
    public void update() {
        this.guiContext.tick();

        if (this.worldContext != null) {
            this.worldContext.tick();
        }

        if (this.getParticleEngine() != null) {
            this.getParticleEngine().tick();
        }

        ScreenUtil.tickToasts();

        for (var component : this.components) {
            component.tick();
        }
    }

    @Override
    public boolean onException(Exception error) {
        error.printStackTrace();
        LOGGER.catching(error);
        this.stop();
        return true;
    }

    @Override
    public boolean onError(Error error) {
        LOGGER.catching(error);
        this.stop();
        return true;
    }

    @Override
    public void quit() {
        this.clientEventBus.callEvent(new ClientDisposeEvent(this));
        LOGGER.info("mod disposed.");

        for (var component : this.components) {
            component.clientQuit(this);
        }

        LOGGER.info("game stopped...");
    }

    @Override
    public void render() {
        Window window = this.getWindow();
        DisplayScreenInfo screenInfo = getDisplaySize();

        Screen scr = this.getClientGUIContext().getScreen();

        if (scr != null && (scr.getBackgroundType().shouldRenderWorld())) {
            GLUtil.checkError("pre_world_render");
            GLUtil.checkError("post_world_render");
        }

        for (var component : this.components) {
            if (scr != null && !scr.getBackgroundType().shouldRenderWorld() && component instanceof LevelRenderer) {
                continue;
            }

            component.render(screenInfo, this.timer.interpolatedTime);
        }


        GLUtil.setupOrthogonalCamera(
                0,
                0,
                window.getWidth(),
                window.getHeight(),
                screenInfo.getScreenWidth(),
                screenInfo.getScreenHeight()
        );


        GLUtil.enableDepthTest();
        GL11.glDisable(GL11.GL_FOG);
        GLUtil.enableBlend();
        GLUtil.checkError("pre_screen_render");
        screenInfo = this.getDisplaySize();
        this.guiContext.render(screenInfo, this.timer.interpolatedTime);
        GLUtil.checkError("post_screen_render");
        GLUtil.disableBlend();

        Sync.sync(this.maxFPS);

        if (System.currentTimeMillis() - this.lastGCTime > ClientSettings.TICK_GC.getValue()) {
            //System.gc();
            FontRenderer.ttf().gc();
            FontRenderer.icon().gc();
            this.lastGCTime = System.currentTimeMillis();
        }

        this.getWindow().incrementFrame();
    }

    //----[world-context]-----
    public WorldContext getWorldContext() {
        return worldContext;
    }

    public void setWorldContext(WorldContext context) {
        if (this.worldContext != null) {
            this.worldContext.destroy();
        }

        this.worldContext = context;

        if (context != null) {
            this.particleEngine = new ParticleEngine(context.getWorld());
            this.controller = new PlayerController(this, context.getPlayer());

            getInstance().getClientGUIContext().setScreen(new HUDScreen());//todo: gui context
        } else {
            this.controller = null;
            this.particleEngine = null;

            this.getDeviceEventBus().unregisterEventListener(this.controller);
        }

        for (var component : this.components) {
            component.worldContextChange(context);
        }
    }

    public void setActiveLevelProvider(ActiveLevelProvider activeLevelProvider) {
        this.activeLevelProvider = activeLevelProvider;
    }

    public Optional<ActiveLevelProvider> getActiveLevelProvider() {
        return Optional.of(this.activeLevelProvider);
    }

    //----[Client component API]----
    public void addComponent(ClientComponent component) {
        this.components.add(component);
    }

    public void removeComponent(Class<? extends ClientComponent> t) {
        this.components.removeIf(t::isInstance);
    }

    public <C extends ClientComponent> Optional<C> getComponent(Class<C> t) {
        for (var c : this.components) {
            if (t.isInstance(c)) {
                return Optional.of(t.cast(c));
            }
        }

        return Optional.empty();
    }


    public DisplayScreenInfo getDisplaySize() {
        var window = this.getWindow();
        var scale = ClientSettings.UISetting.getFixedGUIScale();

        return new DisplayScreenInfo(
                (int) Math.max(window.getWidth() / scale, 1),
                (int) Math.max(window.getHeight() / scale, 1),
                (int) (Math.max(window.getWidth() / scale, 1) / 2),
                (int) (Math.max(window.getHeight() / scale, 1) / 2)
        );
    }

    public SimpleEventBus getDeviceEventBus() {
        return this.getClientDeviceContext().getEventBus();
    }

    public NetworkClient getClientIO() {
        return this.clientIO;
    }

    public SimpleEventBus getClientEventBus() {
        return clientEventBus;
    }

    public ParticleEngine getParticleEngine() {
        return this.particleEngine;
    }

    public PlayerController getPlayerController() {
        return this.controller;
    }

    public Session getSession() {
        return this.session;
    }

    public DeviceHolder getClientDeviceContext() {
        return this.getComponent(DeviceHolder.class).orElseThrow();
    }

    public ClientGUIContext getClientGUIContext() {
        return guiContext;
    }

    public GameSetting getSetting() {
        return setting;
    }
}