package ink.flybird.cubecraft.server.service;

import ink.flybird.cubecraft.EnvironmentPath;
import ink.flybird.cubecraft.SharedContext;
import ink.flybird.cubecraft.level.Level;
import ink.flybird.cubecraft.server.CubecraftServer;
import ink.flybird.cubecraft.server.internal.registries.ServerSettingRegistries;
import ink.flybird.cubecraft.world.ChunkProvider;
import ink.flybird.cubecraft.world.ChunkStorage;
import ink.flybird.cubecraft.world.IWorld;
import ink.flybird.cubecraft.world.chunk.ChunkCodec;
import ink.flybird.cubecraft.world.chunk.ChunkPos;
import ink.flybird.cubecraft.world.chunk.WorldChunk;
import ink.flybird.fcommon.file.NBTBuilder;
import ink.flybird.jflogger.ILogger;
import ink.flybird.jflogger.LogManager;
import ink.flybird.fcommon.nbt.NBTTagCompound;
import ink.flybird.fcommon.registry.TypeItem;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.Options;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;


@TypeItem("cubecraft:chunk_io")
public class DBChunkService implements Service, ChunkProvider, ChunkStorage {
    public static final Options DEFAULT_LEVELDB_OPTIONS = new Options().createIfMissing(true).blockSize(1024);
    public static final int DEFAULT_DB_LIFETIME = 150;
    private static final ILogger LOGGER = LogManager.getLogger("server_db_chunk_io");
    private final HashMap<String, DB> cachedDatabase = new HashMap<>(16);
    private final HashMap<String, Integer> lifetime = new HashMap<>(16);

    private ExecutorService taskPool;
    private String serverLevelName;

    @Override
    public void initialize(CubecraftServer server) {
        Level level = server.getLevel();
        this.serverLevelName = level.getLevelInfo().getLevelName();
        this.taskPool = Executors.newFixedThreadPool(ServerSettingRegistries.CHUNK_IO_THREAD.getValue());
    }

    @Override
    public void onServerTick(CubecraftServer server) {
        for (String db : this.lifetime.keySet()) {
            int i = this.lifetime.get(db);
            if (i <= 0) {
                try {
                    this.getDataBase(db).close();
                } catch (IOException error) {
                    LOGGER.error(error);
                }
                this.getDataBase(db);
                continue;
            }
            this.lifetime.put(db, i - 1);
        }
    }

    @Override
    public void stop(CubecraftServer server) {
        for (DB db : this.cachedDatabase.values()) {
            try {
                db.close();
            } catch (IOException e) {
                LOGGER.error(e);
            }
        }
        this.taskPool.shutdown();
        while (this.taskPool.isShutdown()) {
            Thread.yield();
        }
    }


    @Override
    public WorldChunk getChunk(IWorld world, ChunkPos pos) {
        WorldChunk chunk = new WorldChunk(world, pos);

        if (loadChunkFromDB(chunk)) {
            return chunk;
        }

        //todo:generate_chunk

        return chunk;
    }

    @Override
    public void save(WorldChunk chunk) {
        try {
            NBTTagCompound tag = ChunkCodec.getWorldChunkData(chunk);

            ByteArrayOutputStream stream = new ByteArrayOutputStream(4096);
            GZIPOutputStream zipOutput = new GZIPOutputStream(stream);
            NBTBuilder.write(tag, new DataOutputStream(zipOutput));
            zipOutput.close();
            stream.close();

            byte[] data = stream.toByteArray();
            byte[] key = chunk.getKey().toString().getBytes(StandardCharsets.UTF_8);

            this.getDataBase(chunk.getWorld().getID()).put(key, data);
        } catch (Exception error) {
            LOGGER.error(error);
        }
    }

    @Override
    public ExecutorService getAsyncService() {
        return this.taskPool;
    }


    public DB getDataBase(String worldID) {
        if (this.cachedDatabase.containsKey(worldID)) {
            return this.cachedDatabase.get(worldID);
        }
        try {
            DB db = SharedContext.LEVELDB_FACTORY.open(
                    EnvironmentPath.getChunkDBFile(worldID, this.serverLevelName),
                    DEFAULT_LEVELDB_OPTIONS
            );
            this.cachedDatabase.put(worldID, db);
            this.lifetime.put(worldID, DEFAULT_DB_LIFETIME);

            return db;
        } catch (IOException e) {
            LOGGER.error(e);
        }
        return null;
    }

    public boolean loadChunkFromDB(WorldChunk chunk) {
        try {

            byte[] key = chunk.getKey().toString().getBytes(StandardCharsets.UTF_8);
            byte[] data = this.getDataBase(chunk.getWorld().getID()).get(key);
            if (data == null) {
                return false;
            }

            ByteArrayInputStream stream = new ByteArrayInputStream(data);
            GZIPInputStream zipInput = new GZIPInputStream(stream);
            NBTTagCompound tag = (NBTTagCompound) NBTBuilder.read(new DataInputStream(zipInput));
            zipInput.close();
            stream.close();

            ChunkCodec.setWorldChunkData(chunk, tag);
            return true;
        } catch (Exception error) {
            LOGGER.error(error);
        }
        return false;
    }
}
