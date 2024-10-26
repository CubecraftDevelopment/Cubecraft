package net.cubecraft.server.service;

import me.gb2022.commons.event.EventHandler;
import me.gb2022.commons.nbt.NBT;
import me.gb2022.commons.nbt.NBTTagCompound;
import me.gb2022.commons.registry.TypeItem;
import net.cubecraft.EnvironmentPath;
import net.cubecraft.SharedContext;
import net.cubecraft.level.Level;
import net.cubecraft.server.CubecraftServer;
import net.cubecraft.server.ServerStartupFailedException;
import net.cubecraft.server.event.world.ServerWorldInitializedEvent;
import net.cubecraft.server.internal.registries.ServerSettingRegistries;
import net.cubecraft.world.World;
import net.cubecraft.world.chunk.pos.WorldChunkPos;
import net.cubecraft.world.entity.Entity;
import net.cubecraft.world.storage.PersistentEntityHolder;
import net.cubecraft.world.worldGen.pipeline.ChunkGeneratorPipeline;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.Options;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@TypeItem("cubecraft:entity_service")
public final class EntityService implements Service, PersistentEntityHolder {
    public static final Options DEFAULT_LEVELDB_OPTIONS = new Options().createIfMissing(true).blockSize(1024);
    private static final Logger LOGGER = LogManager.getLogger("server/chunk_service");
    private final HashMap<String, ChunkGeneratorPipeline> pipelineCache = new HashMap<>();
    private DB db;
    private ExecutorService saveTaskPool;
    private String serverLevelName;
    private long lastSaved;

    @Override
    public void initialize(CubecraftServer server) {
        Level level = server.getLevel();
        this.serverLevelName = level.getLevelInfo().getLevelName();
        this.saveTaskPool = Executors.newFixedThreadPool(ServerSettingRegistries.CHUNK_SAVE_THREAD.getValue());
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
                    EnvironmentPath.getEntityDBFile(this.serverLevelName),
                    DEFAULT_LEVELDB_OPTIONS
            );
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }


    @EventHandler
    public void onServerWorldInitialized(ServerWorldInitializedEvent e) {
        World world = e.getWorld();
        world.getLevel().setPersistentEntityHolder(this);
    }


    @Override
    public boolean load(Entity e) {
        while (this.db == null) {
            Thread.yield();
        }
        var data = this.db.get((e.getUuid()).getBytes(StandardCharsets.UTF_8));

        if (data == null) {
            return false;
        }

        e.setData((NBTTagCompound) NBT.readZipped(new ByteArrayInputStream(data)));

        return true;
    }

    @Override
    public void save(Entity entity) {
        this.saveTaskPool.submit(() -> {
            while (this.db == null) {
                Thread.yield();
            }
            try {
                NBTTagCompound tag = entity.getData();

                ByteArrayOutputStream stream = new ByteArrayOutputStream(4096);
                NBT.writeZipped(tag, new DataOutputStream(stream));
                stream.close();

                byte[] data = stream.toByteArray();
                byte[] key = (entity.getUuid()).getBytes(StandardCharsets.UTF_8);

                this.db.put(key, data);
            } catch (Exception error) {
                LOGGER.throwing(error);
            }
        });
    }
}
