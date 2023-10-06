package ink.flybird.cubecraft.client;

import ink.flybird.cubecraft.auth.Session;
import ink.flybird.cubecraft.client.event.app.ClientDisposeEvent;
import ink.flybird.cubecraft.client.event.app.ClientSetupEvent;
import ink.flybird.cubecraft.client.event.gui.context.GUIContextPreInitEvent;
import ink.flybird.cubecraft.client.gui.GUIContext;
import ink.flybird.cubecraft.client.gui.GUIRegistry;
import ink.flybird.cubecraft.client.gui.ScreenUtil;
import ink.flybird.cubecraft.client.gui.base.DisplayScreenInfo;
import ink.flybird.cubecraft.client.gui.screen.*;
import ink.flybird.cubecraft.client.internal.handler.PlayerController;
import ink.flybird.cubecraft.client.registry.ClientSettingRegistry;
import ink.flybird.cubecraft.client.registry.ResourceRegistry;
import ink.flybird.cubecraft.client.registry.TextureRegistry;
import ink.flybird.cubecraft.client.net.ClientIO;
import ink.flybird.cubecraft.client.net.RakNetClientIO;
import ink.flybird.cubecraft.client.render.LevelRenderer;
import ink.flybird.cubecraft.extension.ModManager;
import ink.flybird.cubecraft.resource.ResourceLocation;
import ink.flybird.cubecraft.client.world.ClientChunkProvider;
import ink.flybird.cubecraft.client.world.ClientWorldManager;
import ink.flybird.cubecraft.extension.ExtensionInitializationOperation;
import ink.flybird.cubecraft.internal.entity.EntityPlayer;
import ink.flybird.cubecraft.level.Level;
import ink.flybird.cubecraft.level.LevelInfo;
import ink.flybird.cubecraft.EnvironmentPath;
import ink.flybird.cubecraft.SharedContext;
import ink.flybird.cubecraft.world.IWorld;
import ink.flybird.cubecraft.world.entity.particle.ParticleEngine;
import ink.flybird.fcommon.GameSetting;
import ink.flybird.fcommon.event.EventBus;
import ink.flybird.fcommon.event.SimpleEventBus;
import ink.flybird.jflogger.ILogger;
import ink.flybird.jflogger.LogManager;
import ink.flybird.fcommon.logging.LoggerContext;
import ink.flybird.fcommon.threading.TaskProgressUpdateListener;
import ink.flybird.fcommon.timer.Timer;
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
import org.lwjgl.glfw.GLFW;

//todo:add net support
//todo:add inventory support
//todo:fix smooth light engine

//todo:修复关闭渲染器时导致的严重堆外泄露（VertexBuilder）
//todo:升级quantum3d2

public final class CubecraftClient extends GameApplication implements TaskProgressUpdateListener {
    private static final ILogger LOGGER = LogManager.getLogger("client-main");

    public static final String VERSION = "0.3.8";
    public static CubecraftClient CLIENT;

    private final EventBus deviceEventBus = new SimpleEventBus();
    private final EventBus clientEventBus = new SimpleEventBus();

    private final GameSetting setting = new GameSetting(EnvironmentPath.CONFIG_FOLDER + "/client_setting.properties", "cubecraft client " + VERSION);

    private final Session session = new Session("CubeVlmu", "cubecraft:default");
    private final GUIContext guiManager = new GUIContext(SharedContext.FAML_READER, this, this.getWindow());
    private final ClientIO clientIO = new RakNetClientIO();
    private final ClientWorldManager clientWorldManager = new ClientWorldManager(this);
    public boolean isDebug;
    public LevelRenderer levelRenderer;
    public PlayerController controller;

    private Keyboard keyboard;
    private Mouse mouse;

    private IWorld clientWorld;
    private final ClientChunkProvider clientChunkProvider = new ClientChunkProvider(null, clientWorld);
    private LogoLoadingScreen logoLoadingScreen;
    private ParticleEngine particleEngine;
    private EntityPlayer player;
    private LevelInfo clientLevelInfo;
    private Level level;


    private int frameTime, tickTime;
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

        window.setTitle("Cubecraft-" + VERSION);
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
        ModManager modManager=SharedContext.MOD;

        modManager.registerInternalMod("/client_mod_info.properties");
        modManager.registerInternalMod("/content_mod_info.properties");
        modManager.registerInternalMod("/server_mod_info.properties");
        modManager.loadMods(this);

        LOGGER.info("mod initialized.");

        modManager.getModLoaderEventBus().callEvent(new ClientSetupEvent(this));




        long last = System.currentTimeMillis();
        LOGGER.info("initializing client");
        CLIENT = this;
        this.setting.read();
        ScreenUtil.init(this);
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

        ClientSharedContext.RESOURCE_MANAGER.getEventBus().registerEventListener(TextureRegistry.class);
        ClientSharedContext.RESOURCE_MANAGER.getEventBus().registerEventListener(ResourceRegistry.class);
        ClientSharedContext.RESOURCE_MANAGER.reload();
        ClientSharedContext.RESOURCE_MANAGER.load("default");
        LOGGER.info("resource loaded.");



        this.renderAnimationScreen(this.logoLoadingScreen);

        LOGGER.info("done,%dms", System.currentTimeMillis() - last);
        this.guiManager.setScreen(ResourceRegistry.TITLE_SCREEN);
        this.guiManager.disposeHoverScreen();
        if (this.setting.getValueAsBoolean("client.check_update", false)) {
            VersionCheck.check();
        }
    }

    public void renderAnimationScreen(AnimationScreen screen){
        while (screen.isAnimationNotCompleted()) {
            this.render();
            this.getWindow().update();
            Thread.yield();
        }
    }

    @Override
    public void update() {
        long last = System.currentTimeMillis();
        ClientSharedContext.SMOOTH_FONT_RENDERER.update();
        ClientSharedContext.ICON_FONT_RENDERER.update();

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
        if (this.getClientWorld() != null) {
            //this.getClientWorld().tick();
        }
        this.tickTime = (int) (System.currentTimeMillis() - last);
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
        long last = System.currentTimeMillis();
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
        this.frameTime = (int) (System.currentTimeMillis() - last);
        Sync.sync(ClientSettingRegistry.MAX_FPS.getValue());

        if (System.currentTimeMillis()- this.lastGCTime >ClientSettingRegistry.TICK_GC.getValue()) {
            System.gc();
            this.lastGCTime=System.currentTimeMillis();
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

    @Override
    public void onException(Exception error) {
        error.printStackTrace();
        LOGGER.error(error);
        this.stop();
    }

    @Override
    public void onError(Error error) {
        LOGGER.error(error);
        this.stop();
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

    public GameSetting getGameSetting() {
        return this.setting;
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

    public void setClientLevelInfo(LevelInfo clientLevelInfo) {
        this.clientLevelInfo = clientLevelInfo;
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
        this.levelRenderer = new LevelRenderer(this.getClientWorld(), this.player, this, ResourceLocation.worldRendererSetting(this.clientWorld.getID() + ".json"));
        CLIENT.getGuiManager().setScreen(new HUDScreen());
        this.player.setPos(2147483648L, 600, 0);
        this.getClientWorld().addEntity(this.player);
        this.controller = new PlayerController(this, this.player);
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
}