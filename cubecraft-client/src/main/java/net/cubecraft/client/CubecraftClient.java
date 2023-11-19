package net.cubecraft.client;

import ink.flybird.fcommon.event.EventBus;
import ink.flybird.fcommon.event.SimpleEventBus;
import ink.flybird.fcommon.logging.LoggerContext;
import ink.flybird.fcommon.threading.TaskProgressUpdateListener;
import ink.flybird.fcommon.timer.Timer;
import ink.flybird.jflogger.ILogger;
import ink.flybird.jflogger.LogManager;
import ink.flybird.quantum3d.GameApplication;
import ink.flybird.quantum3d.device.DeviceContext;
import ink.flybird.quantum3d.device.Keyboard;
import ink.flybird.quantum3d.device.Mouse;
import ink.flybird.quantum3d.device.Window;
import ink.flybird.quantum3d.device.adapter.KeyboardEventAdapter;
import ink.flybird.quantum3d.device.adapter.MouseEventAdapter;
import ink.flybird.quantum3d.render.RenderContext;
import ink.flybird.quantum3d_legacy.ContextManager;
import ink.flybird.quantum3d_legacy.GLUtil;
import ink.flybird.quantum3d_legacy.draw.VertexBuilderAllocator;
import ink.flybird.quantum3d_legacy.platform.Sync;
import net.cubecraft.SharedContext;
import net.cubecraft.auth.Session;
import net.cubecraft.client.event.app.ClientDisposeEvent;
import net.cubecraft.client.event.app.ClientPostSetupEvent;
import net.cubecraft.client.event.app.ClientSetupEvent;
import net.cubecraft.client.event.gui.context.GUIContextPreInitEvent;
import net.cubecraft.client.gui.GUIContext;
import net.cubecraft.client.gui.GUIRegistry;
import net.cubecraft.client.gui.ScreenUtil;
import net.cubecraft.client.gui.base.DisplayScreenInfo;
import net.cubecraft.client.gui.screen.*;
import net.cubecraft.client.internal.handler.PlayerController;
import net.cubecraft.client.net.ClientIO;
import net.cubecraft.client.net.RakNetClientIO;
import net.cubecraft.client.registry.ClientSettingRegistry;
import net.cubecraft.client.registry.ResourceRegistry;
import net.cubecraft.client.registry.TextureRegistry;
import net.cubecraft.client.render.LevelRenderer;
import net.cubecraft.client.world.ClientWorldManager;
import net.cubecraft.extension.ExtensionInitializationOperation;
import net.cubecraft.extension.ModManager;
import net.cubecraft.internal.entity.EntityPlayer;
import net.cubecraft.level.Level;
import net.cubecraft.resource.ResourceLocation;
import net.cubecraft.util.VersionInfo;
import net.cubecraft.world.IWorld;
import net.cubecraft.world.entity.particle.ParticleEngine;
import org.lwjgl.glfw.GLFW;

//todo:add net support
//todo:add inventory support
//todo:fix smooth light engine
//todo:quantum3d2

public final class CubecraftClient extends GameApplication implements TaskProgressUpdateListener {
    public static final VersionInfo VERSION = new VersionInfo("client-0.3.9-b3");

    private static final ILogger LOGGER = LogManager.getLogger("client-main");
    public static CubecraftClient CLIENT;

    private final EventBus clientEventBus = new SimpleEventBus();
    private final Session session = new Session("GrassBlock2022", "cubecraft:default");
    private final ClientIO clientIO = new RakNetClientIO();
    private final EventBus deviceEventBus = new SimpleEventBus();
    private final GUIContext guiManager = new GUIContext(SharedContext.FAML_READER, this, this.getWindow());
    private final ClientWorldManager clientWorldManager = new ClientWorldManager(this);
    public boolean isDebug;
    private Keyboard keyboard;
    private Mouse mouse;
    private LevelRenderer levelRenderer;
    private LogoLoadingScreen logoLoadingScreen;
    private Level level;
    private IWorld clientWorld;

    private ParticleEngine particleEngine;
    private EntityPlayer player;
    private PlayerController controller;

    private long lastGCTime;

    public CubecraftClient(DeviceContext deviceContext, RenderContext renderContext, Timer timer) {
        super(deviceContext, renderContext, timer);
        deviceContext.initContext();
    }

    @Override
    public void initDevice(Window window) {
        ClientSharedContext.CLIENT_SETTING.load();
        ClientSharedContext.CLIENT_SETTING.register(ClientSettingRegistry.class);
        ClientSharedContext.CLIENT_SETTING.setEventBus(this.getClientEventBus());
        LOGGER.info("config initialized.");

        ClientSharedContext.RESOURCE_MANAGER.registerResources(ResourceRegistry.class);
        ClientSharedContext.RESOURCE_MANAGER.loadAsync("client:startup");
        LOGGER.info("pre-load resources loaded.");

        this.keyboard = this.getDeviceContext().keyboard(window);
        this.keyboard.create();
        this.keyboard.addListener(new KeyboardEventAdapter(this.deviceEventBus));
        this.mouse = this.getDeviceContext().mouse(window);
        this.mouse.create();
        this.mouse.addListener(new MouseEventAdapter(this.deviceEventBus));

        window.setTitle("Cubecraft-" + CubecraftClient.VERSION.shortVersion());
        window.setSize(1280, 720);
        window.setFullscreen(ClientSettingRegistry.FULL_SCREEN.getValue());
        window.setResizeable(true);
        window.setVsync(ClientSettingRegistry.V_SYNC.getValue());
        window.setIcon(ResourceRegistry.GAME_ICON.getStream());

        GLFW.glfwWindowHint(GLFW.GLFW_SCALE_TO_MONITOR, GLFW.GLFW_TRUE);

        LOGGER.info("window initialized.");
    }

    @Override
    public void initialize() {
        ModManager modManager = SharedContext.MOD;

        modManager.registerInternalMod("/client_mod_info.properties");
        modManager.registerInternalMod("/content_mod_info.properties");
        modManager.registerInternalMod("/server_mod_info.properties");
        modManager.loadMods(this);

        LOGGER.info("mod initialized.");

        modManager.getModLoaderEventBus().callEvent(new ClientSetupEvent(this));
        long last = System.currentTimeMillis();
        LOGGER.info("initializing client");
        CLIENT = this;
        this.timer = new Timer(20);
        LOGGER.info("initializing render system");
        ContextManager.createLegacyGLContext();
        ContextManager.setGLContextVersion(4, 6);
        VertexBuilderAllocator.PREFER_MODE.set(0);
        if (!ClientSettingRegistry.SKIP_STUDIO_LOGO.getValue()) {
            StudioLoadingScreen scr = new StudioLoadingScreen();
            this.guiManager.setScreen(scr);
            this.renderAnimationScreen(scr);
        }
        GUIRegistry.initialize();
        this.clientEventBus.callEvent(new GUIContextPreInitEvent(this.guiManager));

        this.logoLoadingScreen = new LogoLoadingScreen();
        this.guiManager.setHoverScreen(this.logoLoadingScreen);
        this.guiManager.displayHoverScreen();
        LOGGER.info("ui initialized.");

        SharedContext.MOD.initialize(ExtensionInitializationOperation.getClientOperationList());
        LOGGER.info("client-side mod loaded.");

        modManager.getModLoaderEventBus().callEvent(new ClientPostSetupEvent(this));

        ClientSharedContext.RESOURCE_MANAGER.getEventBus().registerEventListener(TextureRegistry.class);
        ClientSharedContext.RESOURCE_MANAGER.getEventBus().registerEventListener(ResourceRegistry.class);
        ClientSharedContext.RESOURCE_MANAGER.reload();
        ClientSharedContext.RESOURCE_MANAGER.load("default");

        LOGGER.info("resource loaded.");

        this.renderAnimationScreen(this.logoLoadingScreen);

        LOGGER.info("done,%dms", System.currentTimeMillis() - last);

        this.guiManager.setScreen(ResourceRegistry.TITLE_SCREEN);
        this.guiManager.disposeHoverScreen();
    }

    @Override
    public void update() {
        GUIRegistry.SMOOTH_FONT_RENDERER.update();
        GUIRegistry.ICON_FONT_RENDERER.update();

        if (this.player != null) {
            this.player.tick();
        }
        if (this.getParticleEngine() != null) {
            this.getParticleEngine().tick();
        }

        if (this.levelRenderer != null) {
            GLUtil.checkError("pre_world_render");
            this.levelRenderer.tick();
            GLUtil.checkError("post_world_render");
        }

        this.getGuiManager().tick();
        ScreenUtil.tickToasts();
        if (this.getClientWorld() != null && this.clientWorldManager.isIntegrated()) {
            this.getClientWorld().tick();
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


        this.mouse.destroy();
        this.keyboard.destroy();
        LOGGER.info("game stopped...");
        LoggerContext.getSharedContext().allSave();
    }

    @Override
    public void render() {
        Window window = this.getWindow();
        DisplayScreenInfo screenInfo = getDisplaySize();

        //shortTick world
        Screen scr = this.getGuiManager().getScreen();
        if (scr != null && (scr.getBackgroundType().shouldRenderWorld())) {
            GLUtil.checkError("pre_world_render");
            levelRenderer.render(this.timer.interpolatedTime);
            GLUtil.checkError("post_world_render");
        }
        GLUtil.setupOrthogonalCamera(0, 0, window.getWidth(), window.getHeight(), screenInfo.getScreenWidth(), screenInfo.getScreenHeight());
        GLUtil.enableDepthTest();
        GLUtil.enableBlend();
        GLUtil.checkError("pre_screen_render");
        screenInfo = this.getDisplaySize();
        this.guiManager.render(screenInfo, this.timer.interpolatedTime);
        GLUtil.checkError("post_screen_render");
        GLUtil.disableBlend();
        Sync.sync(ClientSettingRegistry.MAX_FPS.getValue());

        if (System.currentTimeMillis() - this.lastGCTime > ClientSettingRegistry.TICK_GC.getValue()) {
            System.gc();
            this.lastGCTime = System.currentTimeMillis();
        }
    }

    @Override
    public void onProgressChange(int progress) {
        this.logoLoadingScreen.updateProgress(progress / 100f);
    }

    @Override
    public void onProgressStageChanged(String newStage) {
        this.logoLoadingScreen.setText(newStage);
    }

    @Override
    public void refreshScreen() {
        this.shortTick();
        if (System.currentTimeMillis() % 5 == 0) {
            this.guiManager.tick();
        }
    }

    public void renderAnimationScreen(AnimationScreen screen) {
        while (screen.isAnimationNotCompleted()) {
            this.render();
            this.getWindow().update();
            Thread.yield();
        }
    }

    public DisplayScreenInfo getDisplaySize() {
        Window window = this.getWindow();
        double scale = ClientSettingRegistry.GUI_SCALE.getValue();
        return new DisplayScreenInfo(
                (int) Math.max(window.getWidth() / scale, 1),
                (int) Math.max(window.getHeight() / scale, 1),
                (int) (Math.max(window.getWidth() / scale, 1) / 2),
                (int) (Math.max(window.getHeight() / scale, 1) / 2)
        );
    }

    public EventBus getDeviceEventBus() {
        return deviceEventBus;
    }

    public ClientIO getClientIO() {
        return this.clientIO;
    }

    public EventBus getClientEventBus() {
        return clientEventBus;
    }

    public IWorld getClientWorld() {
        return clientWorld;
    }

    public EntityPlayer getPlayer() {
        return player;
    }

    public ParticleEngine getParticleEngine() {
        return this.particleEngine;
    }

    public GUIContext getGuiManager() {
        return guiManager;
    }

    public void joinWorld(IWorld world) {
        this.clientWorld = world;
        this.player = new EntityPlayer(world, this.session);
        this.particleEngine = new ParticleEngine(this.clientWorld);
        this.levelRenderer = new LevelRenderer(this.getClientWorld(), this.player, this, ResourceLocation.worldRendererSetting(this.clientWorld.getId() + ".json"));
        this.getClientWorld().addEntity(this.player);
        this.controller = new PlayerController(this, this.player);
        CLIENT.getGuiManager().setScreen(new HUDScreen());
    }

    public void leaveWorld() {
        this.clientWorld = null;
        this.player = null;
        this.levelRenderer.stop();
        this.levelRenderer = null;
        this.controller = null;
        this.particleEngine = null;
    }

    public Keyboard getKeyboard() {
        return this.keyboard;
    }

    public Mouse getMouse() {
        return this.mouse;
    }

    public PlayerController getPlayerController() {
        return this.controller;
    }

    public Level getLevel() {
        return this.level;
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    public Session getSession() {
        return this.session;
    }

    public ClientWorldManager getClientWorldManager() {
        return this.clientWorldManager;
    }
}