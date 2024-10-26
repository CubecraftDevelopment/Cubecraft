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
import net.cubecraft.world.chunk.ChunkCodec;
import net.cubecraft.world.chunk.WorldChunk;
import net.cubecraft.world.chunk.pos.ChunkPos;
import net.cubecraft.world.chunk.pos.WorldChunkPos;
import net.cubecraft.world.storage.PersistentChunkHolder;
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
import java.util.zip.GZIPInputStream;

@TypeItem("cubecraft:chunk_service")
public final class ChunkService implements Service, PersistentChunkHolder {
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
                    EnvironmentPath.getChunkDBFile(this.serverLevelName),
                    DEFAULT_LEVELDB_OPTIONS
            );
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public NBTTagCompound loadChunkFromDB(WorldChunkPos pos) {
        while (this.db == null) {
            Thread.yield();
        }
        try {
            byte[] key = (pos.toString()).getBytes(StandardCharsets.UTF_8);
            byte[] data = this.db.get(key);
            if (data == null) {
                return null;
            }

            ByteArrayInputStream stream = new ByteArrayInputStream(data);
            GZIPInputStream zipInput = new GZIPInputStream(stream);
            NBTTagCompound tag = (NBTTagCompound) NBT.read(new DataInputStream(zipInput));
            zipInput.close();
            stream.close();

            return tag;
        } catch (Exception error) {
            LOGGER.throwing(error);
            return null;
        }
    }

    @EventHandler
    public void onServerWorldInitialized(ServerWorldInitializedEvent e) {
        World world = e.getWorld();
        world.getWorldGenerator().setPersistentChunkProvider(this);
    }


    @Override
    public void save(WorldChunk chunk) {
        this.saveTaskPool.submit(() -> {
            while (this.db == null) {
                Thread.yield();
            }
            try {
                NBTTagCompound tag = ChunkCodec.getWorldChunkData(chunk);

                ByteArrayOutputStream stream = new ByteArrayOutputStream(4096);
                NBT.writeZipped(tag, new DataOutputStream(stream));
                stream.close();

                byte[] data = stream.toByteArray();
                byte[] key = (chunk.getWorld().getId() + "@" + chunk.getKey().pack()).getBytes(StandardCharsets.UTF_8);

                this.db.put(key, data);
            } catch (Exception error) {
                LOGGER.throwing(error);
            }
        });
    }

    @Override
    public WorldChunk load(World world, int x, int z) {
        while (this.db == null) {
            Thread.yield();
        }
        var data = this.db.get((world.getId() + "@" + ChunkPos.encode(x, z)).getBytes(StandardCharsets.UTF_8));

        if (data == null) {
            return null;
        }

        var chunk = new WorldChunk(world, new ChunkPos(x, z));
        ChunkCodec.setWorldChunkData(chunk, (NBTTagCompound) NBT.readZipped(new ByteArrayInputStream(data)));
        return chunk;
    }

}
