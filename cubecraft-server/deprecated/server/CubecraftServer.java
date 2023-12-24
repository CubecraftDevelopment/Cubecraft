package ink.flybird.cubecraft.server.server;

import ink.flybird.cubecraft.server.net.RakNetServerIO;
import ink.flybird.fcommon.event.SimpleEventBus;
import ink.flybird.cubecraft.level.Level;
import ink.flybird.cubecraft.level.LevelInfoFactory;
import ink.flybird.cubecraft.register.EnvironmentPath;
import ink.flybird.cubecraft.server.net.ServerIO;
import ink.flybird.cubecraft.server.service.ServiceManager;
import ink.flybird.cubecraft.server.world.ServerWorldFactory;
import ink.flybird.cubecraft.world.IWorld;
import ink.flybird.cubecraft.world.entity.Entity;

import ink.flybird.fcommon.event.EventBus;
import ink.flybird.jflogger.ILogger;
import ink.flybird.jflogger.LogManager;
import ink.flybird.fcommon.logging.LoggerContext;
import ink.flybird.fcommon.logging.SimpleLogger;
import ink.flybird.fcommon.threading.LoopTickingThread;
import ink.flybird.fcommon.timer.Timer;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//todo:写你的内嵌服务端去
public class CubecraftServer extends LoopTickingThread {
    public static final String VERSION = "0.2.4";
    private final Level level;
    private final GameSetting setting = new GameSetting(EnvironmentPath.CONFIG_FOLDER + "/server_setting.properties", "Cubecraft-server");
    private final ExecutorService worldTickingService = Executors.newFixedThreadPool(this.setting.getValueAsInt("server.worldTickThread", 1));

    private final ServiceManager services=new ServiceManager();


    private final InetSocketAddress localAddress;

    private final ServerIO serverIO = new RakNetServerIO();
    private final PlayerTable playerTable = new PlayerTable();
    private final EventBus eventBus = new SimpleEventBus();
    private final ILogger logger = LogManager.getLogger("Server");
    private String levelName;
    private ServerStatus status = ServerStatus.STOPPING;

    public CubecraftServer(InetSocketAddress localAddress, String levelName) {
        this.localAddress = localAddress;
        this.levelName = levelName;
        this.level = new Level(LevelInfoFactory.create(this.levelName), new ServerWorldFactory(this));
        this.level.createLevelFolder();
    }

    public CubecraftServer(InetSocketAddress localAddress, Level initialLevel) {
        this.localAddress = localAddress;
        this.level = initialLevel;
    }

    @Override
    public void init() {


        long startTime = System.currentTimeMillis();
        ServerRegistries.SERVER = this;
        this.status = ServerStatus.STARTUP;

        this.serverIO.start(this.localAddress.getPort(), this.setting.getValueAsInt("network.max_connection", 128));
        this.logger.info("starting server on" + this.localAddress);

        this.timer = new Timer(20);
        this.logger.info("loading world...");

        for (IWorld dim : this.level.dims.values()) {
            dim.tick();
        }
        this.logger.info("done," + ((System.currentTimeMillis() - startTime) / 1000d) + "ms");
        this.status = ServerStatus.RUNNING;
    }

    @Override
    public void shortTick() {

    }

    @Override
    public void tick() {
        //todo:update server
    }

    @Override
    public void stop() {
        this.services.stopAll();
        this.status = ServerStatus.STOPPING;
        this.setRunning(false);
        this.logger.info("server stopped...");
        LoggerContext.getSharedContext().allSave();
    }

    @Override
    public void onException(Exception exception) {
        this.logger.exception(exception);
    }

    @Override
    public void onError(Error error) {
        this.logger.error(error);
    }





    public ServerStatus getStatus() {
        return status;
    }

    public PlayerTable getPlayers() {
        return playerTable;
    }

    public EventBus getEventBus() {
        return eventBus;
    }

    public Level getLevel() {
        return this.level;
    }

    public IWorld getDim(String id) {
        return this.getLevel().getDims().get(id);
    }

    public Entity getEntity(String uuid) {
        Entity e;
        for (IWorld world : this.level.dims.values()) {
            e = world.getEntity(uuid);
            if (e != null) {
                return e;
            }
        }
        return null;
    }

    public ServerIO getServerIO() {
        return serverIO;
    }

    public String getLevelName() {
        return levelName;
    }

    public InetSocketAddress getLocalAddress() {
        return localAddress;
    }

    public GameSetting getSetting() {
        return setting;
    }
}
