package net.cubecraft.client;

import ink.flybird.quantum3d_legacy.ContextManager;
import ink.flybird.quantum3d_legacy.GLUtil;
import ink.flybird.quantum3d_legacy.draw.VertexBuilderAllocator;
import ink.flybird.quantum3d_legacy.platform.Sync;
import me.gb2022.commons.event.SimpleEventBus;
import me.gb2022.commons.timer.Timer;
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
import net.cubecraft.client.gui.ScreenUtil;
import net.cubecraft.client.gui.base.DisplayScreenInfo;
import net.cubecraft.client.gui.screen.Screen;
import net.cubecraft.client.gui.screen.ScreenBuilder;
import net.cubecraft.client.gui.screen.StudioLoadingScreen;
import net.cubecraft.client.internal.handler.PlayerController;
import net.cubecraft.client.net.base.NetworkClient;
import net.cubecraft.client.net.kcp.KCPNetworkClient;
import net.cubecraft.client.particle.ParticleEngine;
import net.cubecraft.client.registry.ResourceRegistry;
import net.cubecraft.client.registry.TextureRegistry;
import net.cubecraft.client.world.ClientWorldManager;
import net.cubecraft.level.Level;
import net.cubecraft.mod.ModLoader;
import net.cubecraft.mod.ModManager;
import net.cubecraft.util.VersionInfo;
import net.cubecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;

//todo: 堆外释放
public final class CubecraftClient extends GameApplication {
    public static final VersionInfo VERSION = new VersionInfo("client-0.4.1-b3");
    private static final Logger LOGGER = LogManager.getLogger("Client");

    private final ClientDeviceContext deviceContext = new ClientDeviceContext();
    private final ClientRenderContext renderContext = new ClientRenderContext(this);
    private final ClientWorldContext worldContext = new ClientWorldContext(this);
    private final ClientGUIContext guiContext = new ClientGUIContext(this, this.getWindow());

    private final SimpleEventBus clientEventBus = new SimpleEventBus();

    private final NetworkClient clientIO = new KCPNetworkClient();
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

        ClientSharedContext.RESOURCE_MANAGER.getEventBus().registerEventListener(TextureRegistry.class);
        ClientSharedContext.RESOURCE_MANAGER.getEventBus().registerEventListener(ResourceRegistry.class);
        ClientSharedContext.RESOURCE_MANAGER.registerResources(ResourceRegistry.class);
        ClientSharedContext.RESOURCE_MANAGER.load("client:startup");
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
        this.timer = new Timer(20);

        ModManager modManager = SharedContext.MOD;
        ModLoader.loadClientInternalMod();
        ModLoader.loadStandaloneMods(EnvironmentPath.getModFolder());
        modManager.completeModRegister(Side.CLIENT);
        modManager.constructMods(null);
        LOGGER.info("mods initialized.");

        this.renderContext.init();
        this.guiContext.init();

        if (!net.cubecraft.client.ClientSettingRegistry.SKIP_STUDIO_LOGO.getValue()) {
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

        ClientSharedContext.RESOURCE_MANAGER.reload();
        ClientSharedContext.RESOURCE_MANAGER.load("default");
        LOGGER.info("resource loaded.");

        this.guiContext.renderAnimationLoadingScreen();

        LOGGER.info("done, %dms".formatted(System.currentTimeMillis() - last));

        this.guiContext.setScreen(ScreenBuilder.xml(ResourceRegistry.TITLE_SCREEN));
        this.guiContext.disposeHoverScreen();
    }

    @Override
    public void update() {
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
        LOGGER.throwing(error);
        this.stop();
        return true;
    }

    @Override
    public boolean onError(Error error) {
        LOGGER.throwing(error);
        this.stop();
        return true;
    }

    @Override
    public void quit() {
        this.clientEventBus.callEvent(new ClientDisposeEvent(this));
        LOGGER.info("mod disposed.");

        this.deviceContext.destroy();
        LOGGER.info("game stopped...");
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

        GLUtil.setupOrthogonalCamera(
                0,
                0,
                window.getWidth(),
                window.getHeight(),
                screenInfo.getScreenWidth(),
                screenInfo.getScreenHeight()
        );
        GLUtil.enableDepthTest();
        GLUtil.enableBlend();
        GLUtil.checkError("pre_screen_render");
        screenInfo = this.getDisplaySize();
        this.guiContext.render(screenInfo, this.timer.interpolatedTime);
        GLUtil.checkError("post_screen_render");
        GLUtil.disableBlend();
        Sync.sync(this.maxFPS);

        if (System.currentTimeMillis() - this.lastGCTime > ClientSettingRegistry.TICK_GC.getValue()) {
            //System.gc();
            ClientGUIContext.FONT_RENDERER.gc();
            ClientGUIContext.ICON_FONT_RENDERER.gc();
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

    public void setWorld(World world) {
        this.worldContext.joinWorld(world);
        this.particleEngine = new ParticleEngine(this.getClientWorldContext().getWorld());

        this.renderContext.joinWorld(world);
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