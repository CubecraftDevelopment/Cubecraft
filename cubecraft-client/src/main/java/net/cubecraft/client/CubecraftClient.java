package net.cubecraft.client;

import ink.flybird.fcommon.event.EventBus;
import ink.flybird.fcommon.event.SimpleEventBus;
import ink.flybird.fcommon.logging.LoggerContext;
import ink.flybird.fcommon.timer.Timer;
import ink.flybird.jflogger.ILogger;
import ink.flybird.jflogger.LogManager;
import ink.flybird.quantum3d_legacy.ContextManager;
import ink.flybird.quantum3d_legacy.GLUtil;
import ink.flybird.quantum3d_legacy.draw.VertexBuilderAllocator;
import ink.flybird.quantum3d_legacy.platform.Sync;
import me.gb2022.quantum3d.GameApplication;
import me.gb2022.quantum3d.device.DeviceContext;
import me.gb2022.quantum3d.device.Window;
import me.gb2022.quantum3d.device.adapter.WindowEventAdapter;
import me.gb2022.quantum3d.render.RenderContext;
import net.cubecraft.EnvironmentPath;
import net.cubecraft.SharedContext;
import net.cubecraft.Side;
import net.cubecraft.auth.Session;
import net.cubecraft.client.context.ClientDeviceContext;
import net.cubecraft.client.context.ClientGUIContext;
import net.cubecraft.client.context.ClientRenderContext;
import net.cubecraft.client.context.ClientWorldContext;
import net.cubecraft.client.event.app.ClientDisposeEvent;
import net.cubecraft.client.event.app.ClientPostSetupEvent;
import net.cubecraft.client.event.app.ClientSetupEvent;
import net.cubecraft.client.event.gui.context.GUIContextPreInitEvent;
import net.cubecraft.client.gui.ScreenUtil;
import net.cubecraft.client.gui.base.DisplayScreenInfo;
import net.cubecraft.client.gui.screen.Screen;
import net.cubecraft.client.gui.screen.StudioLoadingScreen;
import net.cubecraft.client.internal.handler.PlayerController;
import net.cubecraft.client.net.ClientIO;
import net.cubecraft.client.net.RakNetClientIO;
import net.cubecraft.client.registry.ResourceRegistry;
import net.cubecraft.client.registry.TextureRegistry;
import net.cubecraft.client.world.ClientWorldManager;
import net.cubecraft.level.Level;
import net.cubecraft.mod.ModLoader;
import net.cubecraft.mod.ModManager;
import net.cubecraft.util.SystemInfoQuery;
import net.cubecraft.util.VersionInfo;
import net.cubecraft.world.IWorld;
import net.cubecraft.world.entity.particle.ParticleEngine;
import org.lwjgl.glfw.GLFW;

public final class CubecraftClient extends GameApplication {
    public static final VersionInfo VERSION = new VersionInfo("client-0.4.1-b2");
    private static final ILogger LOGGER = LogManager.getLogger("client-main");

    private final ClientDeviceContext deviceContext = new ClientDeviceContext();
    private final ClientRenderContext renderContext = new ClientRenderContext(this);
    private final ClientWorldContext worldContext = new ClientWorldContext(this);
    private final ClientGUIContext guiContext = new ClientGUIContext(this, this.getWindow());

    private final EventBus clientEventBus = new SimpleEventBus();

    private final ClientIO clientIO = new RakNetClientIO();
    private final ClientWorldManager clientWorldManager = new ClientWorldManager(this);
    private final Session session = new Session("GrassBlock2022", "cubecraft:default");

    public int maxFPS = 60;
    public boolean isDebug;
    private PlayerController controller;
    private ParticleEngine particleEngine;
    private long lastGCTime;

    public CubecraftClient(DeviceContext deviceContext, RenderContext renderContext, Timer timer) {
        super(deviceContext, renderContext, timer);
        ClientSharedContext.CLIENT_INSTANCE.setObj(this);
        deviceContext.initContext();
    }

    @Override
    public void initDevice(Window window) {
        ClientSharedContext.CLIENT_SETTING.load();
        ClientSharedContext.CLIENT_SETTING.register(ClientSettingRegistry.class);
        ClientSharedContext.CLIENT_SETTING.setEventBus(this.getClientEventBus());
        this.maxFPS = ClientSettingRegistry.MAX_FPS.getValue();
        LOGGER.info("config initialized.");

        ContextManager.createLegacyGLContext();
        ContextManager.setGLContextVersion(4, 6);
        VertexBuilderAllocator.PREFER_MODE.set(0);
        LOGGER.info("render context started.");

        ClientSharedContext.RESOURCE_MANAGER.registerResources(ResourceRegistry.class);
        ClientSharedContext.RESOURCE_MANAGER.loadAsync("client:startup");
        LOGGER.info("pre-load resources loaded.");

        this.deviceContext.init(window, this.getDeviceContext());

        window.setTitle("Cubecraft-" + CubecraftClient.VERSION.shortVersion());
        window.setSize(1280, 720);
        window.setFullscreen(ClientSettingRegistry.FULL_SCREEN.getValue());
        window.setResizeable(true);
        window.setVsync(ClientSettingRegistry.V_SYNC.getValue());
        window.setIcon(ResourceRegistry.GAME_ICON.getStream());
        window.addListener(new WindowEventAdapter(this.getClientEventBus()));

        GLFW.glfwWindowHint(GLFW.GLFW_SCALE_TO_MONITOR, GLFW.GLFW_TRUE);

        LOGGER.info("window initialized.");
    }

    @Override
    public void initialize() {
        ModManager modManager = SharedContext.MOD;

        ModLoader.loadClientInternalMod();
        ModLoader.loadStandaloneMods(EnvironmentPath.getModFolder());

        modManager.completeModRegister(Side.CLIENT);

        modManager.constructMods(this.guiContext);

        LOGGER.info("mod initialized.");

        modManager.getModLoaderEventBus().callEvent(new ClientSetupEvent(this));
        long last = System.currentTimeMillis();
        LOGGER.info("initializing client");
        this.timer = new Timer(20);
        LOGGER.info("initializing render system");

        if (!net.cubecraft.client.ClientSettingRegistry.SKIP_STUDIO_LOGO.getValue()) {
            StudioLoadingScreen scr = new StudioLoadingScreen();
            this.guiContext.setScreen(scr);
            this.guiContext.renderAnimationScreen(scr);
        }
        ClientGUIContext.initializeContext();
        this.clientEventBus.callEvent(new GUIContextPreInitEvent(this.guiContext));

        this.guiContext.setHoverLoadingScreen();
        this.guiContext.displayHoverScreen();
        LOGGER.info("ui initialized.");

        LOGGER.info("client-side mod loaded.");

        modManager.getModLoaderEventBus().callEvent(new ClientPostSetupEvent(this));

        ClientSharedContext.RESOURCE_MANAGER.getEventBus().registerEventListener(TextureRegistry.class);
        ClientSharedContext.RESOURCE_MANAGER.getEventBus().registerEventListener(ResourceRegistry.class);

        ClientSharedContext.RESOURCE_MANAGER.reload();

        ClientSharedContext.RESOURCE_MANAGER.load("default");

        LOGGER.info("resource loaded.");

        this.guiContext.renderAnimationLoadingScreen();

        LOGGER.info("done,%dms", System.currentTimeMillis() - last);

        this.guiContext.setScreen(ResourceRegistry.TITLE_SCREEN);
        this.guiContext.disposeHoverScreen();
    }

    @Override
    public void update() {
        ClientGUIContext.SMOOTH_FONT_RENDERER.update();
        ClientGUIContext.ICON_FONT_RENDERER.update();

        this.worldContext.tick();
        this.renderContext.tick();
        this.guiContext.tick();

        if (this.getParticleEngine() != null) {
            this.getParticleEngine().tick();
        }

        ScreenUtil.tickToasts();
        if (this.getClientWorldContext().getWorld() != null && this.clientWorldManager.isIntegrated()) {
            this.getClientWorldContext().getWorld().tick();
        }
    }

    @Override
    public boolean onException(Exception error) {
        LOGGER.error(error);
        this.stop();
        return true;
    }

    @Override
    public boolean onError(Error error) {
        LOGGER.error(error);
        this.stop();
        return true;
    }

    @Override
    public void quit() {
        this.clientEventBus.callEvent(new ClientDisposeEvent(this));
        LOGGER.info("mod disposed.");

        this.deviceContext.destroy();
        LOGGER.info("game stopped...");
        LoggerContext.getSharedContext().allSave();
    }

    @Override
    public void render() {
        Window window = this.getWindow();
        DisplayScreenInfo screenInfo = getDisplaySize();

        Screen scr = this.getClientGUIContext().getScreen();

        if (scr != null && (scr.getBackgroundType().shouldRenderWorld())) {
            GLUtil.checkError("pre_world_render");
            this.getClientRenderContext().render(this.timer.interpolatedTime);
            GLUtil.checkError("post_world_render");
        }

        GLUtil.setupOrthogonalCamera(0, 0, window.getWidth(), window.getHeight(), screenInfo.getScreenWidth(), screenInfo.getScreenHeight());
        GLUtil.enableDepthTest();
        GLUtil.enableBlend();
        GLUtil.checkError("pre_screen_render");
        screenInfo = this.getDisplaySize();
        this.guiContext.render(screenInfo, this.timer.interpolatedTime);
        GLUtil.checkError("post_screen_render");
        GLUtil.disableBlend();
        Sync.sync(this.maxFPS);

        if (System.currentTimeMillis() - this.lastGCTime > ClientSettingRegistry.TICK_GC.getValue()) {
            SystemInfoQuery.update();
            System.gc();
            this.lastGCTime = System.currentTimeMillis();
        }
    }

    public DisplayScreenInfo getDisplaySize() {
        Window window = this.getWindow();
        double scale = net.cubecraft.client.ClientSettingRegistry.GUI_SCALE.getValue();
        return new DisplayScreenInfo(
                (int) Math.max(window.getWidth() / scale, 1),
                (int) Math.max(window.getHeight() / scale, 1),
                (int) (Math.max(window.getWidth() / scale, 1) / 2),
                (int) (Math.max(window.getHeight() / scale, 1) / 2)
        );
    }

    public EventBus getDeviceEventBus() {
        return this.getClientDeviceContext().getEventBus();
    }

    public ClientIO getClientIO() {
        return this.clientIO;
    }

    public EventBus getClientEventBus() {
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

    public ClientWorldManager getClientWorldManager() {
        return this.clientWorldManager;
    }


    public void joinLevel(Level level) {
        this.worldContext.joinLevel(level);
        this.renderContext.joinLevel(level);
    }

    public void leaveLevel() {
        this.getDeviceEventBus().unregisterEventListener(this.controller);
        this.controller = null;
        this.particleEngine = null;

        this.worldContext.leaveLevel();
        this.renderContext.leaveLevel();
    }

    public void setWorld(IWorld world) {
        this.worldContext.joinWorld(world);
        this.renderContext.joinWorld(world);

        this.particleEngine = new ParticleEngine(this.getClientWorldContext().getWorld());
        this.controller = new PlayerController(this, this.getClientWorldContext().getPlayer());
    }


    public ClientDeviceContext getClientDeviceContext() {
        return deviceContext;
    }

    public ClientRenderContext getClientRenderContext() {
        return renderContext;
    }

    public ClientWorldContext getClientWorldContext() {
        return this.worldContext;
    }

    public ClientGUIContext getClientGUIContext() {
        return guiContext;
    }
}