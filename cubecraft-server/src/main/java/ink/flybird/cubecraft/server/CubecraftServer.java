package ink.flybird.cubecraft.server;

import ink.flybird.cubecraft.EnvironmentPath;
import ink.flybird.cubecraft.SharedContext;
import ink.flybird.cubecraft.extension.ModManager;
import ink.flybird.cubecraft.level.Level;
import ink.flybird.cubecraft.level.LevelInfo;
import ink.flybird.cubecraft.server.event.ServerSetupEvent;
import ink.flybird.cubecraft.server.net.RakNetServerIO;
import ink.flybird.cubecraft.server.net.ServerIO;
import ink.flybird.cubecraft.server.service.Service;
import ink.flybird.cubecraft.server.world.ServerWorldFactory;
import ink.flybird.cubecraft.util.setting.GameSetting;
import ink.flybird.fcommon.context.LifetimeCounter;
import ink.flybird.fcommon.event.EventBus;
import ink.flybird.fcommon.event.SimpleEventBus;
import ink.flybird.fcommon.logging.LoggerContext;
import ink.flybird.fcommon.threading.LoopTickingThread;
import ink.flybird.fcommon.timer.Timer;
import ink.flybird.jflogger.ILogger;
import ink.flybird.jflogger.LogManager;

import java.net.InetSocketAddress;
import java.util.Map;

public class CubecraftServer extends LoopTickingThread {
    public static final String VERSION = "0.2.4";
    private static final ILogger LOGGER = LogManager.getLogger("Server");
    private final Level level;
    private final GameSetting setting = new GameSetting(EnvironmentPath.CONFIG_FOLDER + "/server_setting.toml");
    private final InetSocketAddress localAddress;
    private final ServerIO serverIO = new RakNetServerIO();
    private final PlayerTable playerTable = new PlayerTable();
    private final EventBus eventBus = new SimpleEventBus();
    private final LifetimeCounter lifetimeCounter = new LifetimeCounter();
    private final boolean isIntegrated;
    private Map<String, Service> services;
    private String levelName;

    public CubecraftServer(InetSocketAddress localAddress, String levelName, boolean isIntegrated) {
        this.localAddress = localAddress;
        this.levelName = levelName;
        this.level = new Level(LevelInfo.create(levelName, 114514), new ServerWorldFactory(this));
        this.isIntegrated = isIntegrated;
        this.level.createLevelFolder();
    }

    public CubecraftServer(InetSocketAddress localAddress, Level initialLevel, boolean isIntegrated) {
        this.localAddress = localAddress;
        this.level = initialLevel;
        this.isIntegrated = isIntegrated;
    }


    @Override
    public void init() {
        long startTime = System.currentTimeMillis();

        ServerSharedContext.SERVER = this;
        this.setting.load();
        this.timer = new Timer(20);
        LOGGER.info("config loaded.");

        ModManager modManager = SharedContext.MOD;
        if (!this.isIntegrated) {
            modManager.registerInternalMod("/content_mod_info.properties");
            modManager.registerInternalMod("/server_mod_info.properties");
            modManager.loadMods(null);
        }
        modManager.getModLoaderEventBus().callEvent(new ServerSetupEvent(this));

        this.serverIO.start(this.localAddress.getPort(), 128);
        LOGGER.info("service started on %s.", this.localAddress);


        this.services = ServerSharedContext.SERVICE.createAll();
        for (Service service : this.services.values()) {
            try {
                service.initialize(this);
            } catch (Exception e) {
                LOGGER.error("find error when initializing service :(");
                LOGGER.error(e);
            }
        }
        LOGGER.info("services initialized.");

        LOGGER.info("world loaded.");

        this.lifetimeCounter.allocate();
        LOGGER.info("server started in %d ms.", ((System.currentTimeMillis() - startTime)));
    }

    @Override
    public void tick() {
        for (Service service : this.services.values()) {
            service.onServerTick(this);
        }
    }

    @Override
    public void stop() {
        this.lifetimeCounter.release();
        this.setRunning(false);

        this.serverIO.allCloseConnection();
        LOGGER.info("server netCore stopped.");

        for (Service service : this.services.values()) {
            service.stop(this);
        }
        LOGGER.info("service stopped.");

        LoggerContext.getSharedContext().allSave();
        LOGGER.info("server stopped.");
    }

    @Override
    public void onException(Exception error) {
        LOGGER.error(error);
    }

    @Override
    public void onError(Error error) {
        LOGGER.error(error);
    }


    public EventBus getEventBus() {
        return eventBus;
    }

    public ServerIO getServerIO() {
        return serverIO;
    }

    public GameSetting getSetting() {
        return this.setting;
    }

    public LifetimeCounter getLifetimeCounter() {
        return lifetimeCounter;
    }


    public PlayerTable getPlayers() {
        return playerTable;
    }

    public Level getLevel() {
        return this.level;
    }
}
