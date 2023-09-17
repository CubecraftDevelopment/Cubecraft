package ink.flybird.cubecraft.client;

import ink.flybird.cubecraft.client.event.ClientInitializeEvent;
import ink.flybird.cubecraft.client.event.ClientShutdownEvent;
import ink.flybird.cubecraft.client.gui.GUIManager;
import ink.flybird.cubecraft.client.gui.ScreenUtil;
import ink.flybird.cubecraft.client.gui.base.DisplayScreenInfo;
import ink.flybird.cubecraft.client.gui.screen.Screen;
import ink.flybird.cubecraft.client.gui.screen.ScreenBackgroundType;
import ink.flybird.cubecraft.client.internal.gui.screen.HUDScreen;
import ink.flybird.cubecraft.client.internal.gui.screen.LogoLoadingScreen;
import ink.flybird.cubecraft.client.internal.gui.screen.StudioLoadingScreen;
import ink.flybird.cubecraft.client.internal.handler.PlayerController;
import ink.flybird.cubecraft.client.internal.registry.ClientSettingRegistry;
import ink.flybird.cubecraft.client.internal.registry.ResourceRegistry;
import ink.flybird.cubecraft.client.net.ClientIO;
import ink.flybird.cubecraft.client.net.RakNetClientIO;
import ink.flybird.cubecraft.client.render.LevelRenderer;
import ink.flybird.cubecraft.client.resources.ResourceLocation;
import ink.flybird.cubecraft.client.world.ClientChunkProvider;
import ink.flybird.cubecraft.client.world.ClientWorldManager;
import ink.flybird.fcommon.GameSetting;
import ink.flybird.fcommon.event.EventBus;
import ink.flybird.fcommon.event.SimpleEventBus;
import ink.flybird.fcommon.logging.Logger;
import ink.flybird.fcommon.logging.LoggerContext;
import ink.flybird.fcommon.threading.TaskProgressUpdateListener;
import ink.flybird.fcommon.timer.Timer;
import ink.flybird.quantum3d_legacy.ContextManager;
import ink.flybird.quantum3d_legacy.GLUtil;
import ink.flybird.quantum3d.GameApplication;
import ink.flybird.quantum3d.device.DeviceContext;
import ink.flybird.quantum3d.device.Keyboard;
import ink.flybird.quantum3d.device.Mouse;
import ink.flybird.quantum3d.device.Window;
import ink.flybird.quantum3d.device.adapter.KeyboardEventAdapter;
import ink.flybird.quantum3d.device.adapter.MouseEventAdapter;
import ink.flybird.quantum3d_legacy.draw.VertexBuilderAllocator;
import ink.flybird.quantum3d_legacy.platform.Sync;
import ink.flybird.quantum3d.render.RenderContext;
import ink.flybird.cubecraft.auth.Session;
import ink.flybird.cubecraft.extansion.ExtensionInitializationOperation;
import ink.flybird.cubecraft.internal.entity.EntityPlayer;
import ink.flybird.cubecraft.level.Level;
import ink.flybird.cubecraft.level.LevelInfo;
import ink.flybird.cubecraft.register.EnvironmentPath;
import ink.flybird.cubecraft.register.SharedContext;
import ink.flybird.cubecraft.world.IWorld;
import ink.flybird.cubecraft.world.entity.particle.ParticleEngine;

//todo:add net support
//todo:add inventory support
//todo:fix smooth light engine

//todo:修复关闭渲染器时导致的严重堆外泄露（VertexBuilder）
//todo:升级quantum3d2

public final class CubecraftClient extends GameApplication implements TaskProgressUpdateListener {
    public static final String VERSION = "0.3.8";
    public static CubecraftClient CLIENT;

    private final EventBus deviceEventBus = new SimpleEventBus();


    private final Logger logger = SharedContext.LOG_CONTEXT.createLogger("client_main");
    private final EventBus clientEventBus = new SimpleEventBus();

    private final GameSetting setting = new GameSetting(EnvironmentPath.CONFIG_FOLDER + "/client_setting.properties", "cubecraft client " + VERSION);

    private final Session session = new Session("CubeVlmu", "cubecraft:default");
    private final GUIManager guiManager = new GUIManager(SharedContext.FAML_READER, this, this.getWindow());
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


    public CubecraftClient(DeviceContext deviceContext, RenderContext renderContext, Timer timer) {
        super(deviceContext, renderContext, timer);
        deviceContext.initContext();
    }

    @Override
    public void initDevice(Window window) {
        this.logger.info("load config");
        ClientSharedContext.CLIENT_SETTING.load();
        ClientSharedContext.CLIENT_SETTING.register(ClientSettingRegistry.class);
        ClientSharedContext.CLIENT_SETTING.setEventBus(this.getClientEventBus());


        this.logger.info("pre-load resources");
        ClientSharedContext.RESOURCE_MANAGER.registerResources(ResourceRegistry.class);
        ClientSharedContext.RESOURCE_MANAGER.loadAsync("client:startup");

        this.logger.info("initialize window");
        this.logger.info("creating device");
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
    }

    @Override
    public void initialize() {
        long last = System.currentTimeMillis();
        this.logger.info("initializing client");
        CLIENT = this;
        this.setting.read();
        ScreenUtil.init(this);
        this.timer = new Timer(20);
        this.logger.info("initializing render system");
        ContextManager.createLegacyGLContext();
        ContextManager.setGLContextVersion(4, 6);
        VertexBuilderAllocator.PREFER_MODE.set(0);

        if (!this.setting.getValueAsBoolean("client.boot.skip_studio_logo", true)) {
            StudioLoadingScreen scr = new StudioLoadingScreen(false, "_", ScreenBackgroundType.EMPTY, null);
            this.guiManager.setScreen(scr);
            while (!scr.isAnimationCompleted()) {
                this.render();
                this.getWindow().update();
                Thread.yield();
            }
        }

        this.clientEventBus.callEvent(new ClientInitializeEvent(this));

        this.logger.info("initializing screen");
        this.logoLoadingScreen = new LogoLoadingScreen();
        this.guiManager.setHoverScreen(this.logoLoadingScreen);
        this.guiManager.displayHoverScreen();

        this.logger.info("loading mods...");
        SharedContext.MOD.registerInternalMod("/client_mod_info.properties");
        SharedContext.MOD.registerInternalMod("/content_mod_info.properties");
        SharedContext.MOD.registerInternalMod("/server_mod_info.properties");
        SharedContext.MOD.loadMods(this);
        SharedContext.MOD.initialize(null, ExtensionInitializationOperation.getClientOperationList());
        this.logger.info("loading resources...");
        ClientSharedContext.RESOURCE_MANAGER.reload(this);
        ClientSharedContext.RESOURCE_MANAGER.load("default");

        this.logger.info("done,%dms", System.currentTimeMillis() - last);
        this.guiManager.setScreen("cubecraft:title_screen.xml");
        this.guiManager.disposeHoverScreen();
        if (this.setting.getValueAsBoolean("client.check_update", false)) {
            VersionCheck.check();
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
            this.getClientWorld().tick();
        }
        if (this.getGameSetting().getValueAsBoolean("client.tick_gc", false)) {
            System.gc();
        }
        this.tickTime = (int) (System.currentTimeMillis() - last);
    }

    //todo:资源加载崩溃问题
    @Override
    public void quit() {
        this.clientEventBus.callEvent(new ClientShutdownEvent(this));
        this.mouse.destroy();
        this.keyboard.destroy();
        logger.info("game stopped...");
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
        GLUtil.setupOrthogonalCamera(0, 0, window.getWidth(), window.getHeight(), screenInfo.scrWidth(), screenInfo.scrHeight());
        GLUtil.enableDepthTest();
        GLUtil.enableBlend();
        GLUtil.checkError("pre_screen_render");
        screenInfo = this.getDisplaySize();
        this.guiManager.render(screenInfo, this.timer.interpolatedTime);
        GLUtil.checkError("post_screen_render");
        GLUtil.disableBlend();
        this.frameTime = (int) (System.currentTimeMillis() - last);
        Sync.sync(ClientSettingRegistry.MAX_FPS.getValue());
    }


    //progress
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
    public void onException(Exception exception) {
        exception.printStackTrace();
        this.logger.exception(exception);
        this.stop();
    }

    @Override
    public void onError(Error error) {
        error.printStackTrace();
        this.logger.error(error);
        this.stop();
    }

    //access
    public DisplayScreenInfo getDisplaySize() {
        ink.flybird.quantum3d.device.Window window = this.getWindow();
        int scale = this.setting.getValueAsInt("client.render.gui_scale", 2);
        return new DisplayScreenInfo(
                scale,
                Math.max(window.getWidth() / scale, 1),
                Math.max(window.getHeight() / scale, 1),
                Math.max(window.getWidth() / scale, 1) / 2,
                Math.max(window.getHeight() / scale, 1) / 2
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

    public GUIManager getGuiManager() {
        return guiManager;
    }

    public void joinWorld(IWorld world) {
        this.clientWorld = world;
        this.player = new EntityPlayer(world, this.session);
        this.particleEngine = new ParticleEngine(this.clientWorld);
        this.levelRenderer = new LevelRenderer(this.getClientWorld(), this.player, this, ResourceLocation.worldRendererSetting(this.clientWorld.getID() + ".json"));
        CLIENT.getGuiManager().setScreen(new HUDScreen());
        this.player.setPos(0, 128, 0);
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