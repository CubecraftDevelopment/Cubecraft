package ink.flybird.cubecraft.server.internal.service;

import ink.flybird.cubecraft.EnvironmentPath;
import ink.flybird.cubecraft.SharedContext;
import ink.flybird.cubecraft.server.CubecraftServer;
import ink.flybird.cubecraft.server.internal.registries.ServerSettingRegistries;
import ink.flybird.cubecraft.server.service.AbstractService;
import ink.flybird.cubecraft.world.ChunkProvider;
import ink.flybird.cubecraft.world.ChunkStorage;
import ink.flybird.cubecraft.world.IWorld;
import ink.flybird.cubecraft.world.chunk.ChunkCodec;
import ink.flybird.cubecraft.world.chunk.ChunkPos;
import ink.flybird.cubecraft.world.chunk.WorldChunk;
import ink.flybird.fcommon.file.NBTBuilder;
import ink.flybird.fcommon.logging.Logger;
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
public class ChunkIOService extends AbstractService implements ChunkProvider, ChunkStorage {
    public static final Options DEFAULT_LEVELDB_OPTIONS = new Options().createIfMissing(true).blockSize(1024);
    public static final int DEFAULT_DB_LIFETIME = 150;

    private final Logger logger = SharedContext.LOG_CONTEXT.createLogger("chunk_io");
    private final ExecutorService taskPool;
    private final HashMap<String, DB> cachedDatabase = new HashMap<>(16);
    private final HashMap<String, Integer> lifetime = new HashMap<>(16);

    public ChunkIOService(CubecraftServer server) {
        super(server);
        this.taskPool = Executors.newFixedThreadPool(ServerSettingRegistries.CHUNK_IO_THREAD.getValue());
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
        } catch (Exception exception) {
            this.logger.exception(exception);
        }
    }

    @Override
    public ExecutorService getAsyncService() {
        return this.taskPool;
    }



    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

    @Override
    public void serverTick() {
        this.tickDataBase();
    }


    public void tickDataBase() {
        for (String db : this.lifetime.keySet()) {
            int i = this.lifetime.get(db);
            if (i <= 0) {
                try {
                    this.getDataBase(db).close();
                } catch (IOException exception) {
                    this.logger.exception(exception);
                }
                this.getDataBase(db);
                continue;
            }
            this.lifetime.put(db, i - 1);
        }
    }

    public DB getDataBase(String worldID) {
        if (this.cachedDatabase.containsKey(worldID)) {
            return this.cachedDatabase.get(worldID);
        }
        try {
            DB db = SharedContext.LEVELDB_FACTORY.open(
                    EnvironmentPath.getChunkDBFile(worldID, this.getServer().getLevel().getName()),
                    DEFAULT_LEVELDB_OPTIONS
            );
            this.cachedDatabase.put(worldID, db);
            this.lifetime.put(worldID, DEFAULT_DB_LIFETIME);

            return db;
        } catch (IOException e) {
            this.logger.exception(e);
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
        } catch (Exception exception) {
            this.logger.exception(exception);
        }
        return false;
    }
}
