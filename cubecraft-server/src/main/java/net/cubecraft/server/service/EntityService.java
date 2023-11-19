package net.cubecraft.server.service;

import net.cubecraft.EnvironmentPath;
import net.cubecraft.SharedContext;
import net.cubecraft.level.Level;
import net.cubecraft.server.CubecraftServer;
import net.cubecraft.server.ServerStartupFailedException;
import net.cubecraft.server.event.world.ServerWorldInitializedEvent;
import net.cubecraft.server.internal.registries.ServerSettingRegistries;
import net.cubecraft.world.IWorld;
import net.cubecraft.world.worldGen.pipeline.ChunkGeneratorPipeline;
import ink.flybird.fcommon.event.EventHandler;
import ink.flybird.fcommon.registry.TypeItem;
import ink.flybird.jflogger.ILogger;
import ink.flybird.jflogger.LogManager;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.Options;

import java.io.*;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@TypeItem("cubecraft:entity_service")
public class EntityService implements Service{
    public static final Options DEFAULT_LEVELDB_OPTIONS = new Options().createIfMissing(true).blockSize(1024);
    private static final ILogger LOGGER = LogManager.getLogger("server/entity_service");
    private final HashMap<String, ChunkGeneratorPipeline> pipelineCache = new HashMap<>();
    private DB db;
    private ExecutorService saveTaskPool;
    private ExecutorService loadTaskPool;
    private String serverLevelName;
    private long lastSaved;

    @Override
    public void initialize(CubecraftServer server) {
        Level level = server.getLevel();
        this.serverLevelName = level.getLevelInfo().getLevelName();
        this.saveTaskPool = Executors.newFixedThreadPool(ServerSettingRegistries.CHUNK_SAVE_THREAD.getValue());
        this.loadTaskPool = Executors.newFixedThreadPool(ServerSettingRegistries.CHUNK_LOAD_THREAD.getValue());
        server.getEventBus().registerEventListener(this);
        this.loadDataBase();
    }

    @Override
    public void onServerTick(CubecraftServer server) {
        if (System.currentTimeMillis() - this.lastSaved > 20000) {
            //this.saveDataBase();
            //this.loadDataBase();
            this.lastSaved = System.currentTimeMillis();
        }
    }

    @Override
    public void postStop(CubecraftServer server) {
        this.saveTaskPool.shutdown();
        while (!this.saveTaskPool.isTerminated()) {
            Thread.yield();
        }
        this.saveDataBase();
    }

    public void saveDataBase() {
        if (this.db == null) {
            return;
        }
        try {
            this.db.close();
        } catch (IOException e) {
            throw new ServerStartupFailedException(e);
        }
        this.db = null;
    }

    public void loadDataBase() {
        if (this.db != null) {
            return;
        }
        try {
            this.db = SharedContext.LEVELDB_FACTORY.open(
                    EnvironmentPath.getChunkDBFile(this.serverLevelName),
                    DEFAULT_LEVELDB_OPTIONS
            );
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    @EventHandler
    public void onServerWorldInitialized(ServerWorldInitializedEvent e) {
        IWorld world = e.getWorld();
    }

}
