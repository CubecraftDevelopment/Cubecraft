package net.cubecraft.server.service;

import net.cubecraft.ContentRegistries;
import net.cubecraft.EnvironmentPath;
import net.cubecraft.SharedContext;
import net.cubecraft.level.Level;
import net.cubecraft.server.CubecraftServer;
import net.cubecraft.server.ServerStartupFailedException;
import net.cubecraft.server.event.world.ServerWorldInitializedEvent;
import net.cubecraft.server.internal.registries.ServerSettingRegistries;
import net.cubecraft.world.chunk.ChunkLoader;
import net.cubecraft.world.chunk.ChunkSaver;
import net.cubecraft.world.IWorld;
import net.cubecraft.world.chunk.pos.ChunkPos;
import net.cubecraft.world.chunk.pos.WorldChunkPos;
import net.cubecraft.world.chunk.task.ChunkLoadTicket;
import net.cubecraft.world.worldGen.pipeline.ChunkGenerateTask;
import net.cubecraft.world.worldGen.pipeline.ChunkGeneratorPipeline;
import net.cubecraft.world.worldGen.pipeline.WorldGenPipelineBuilder;
import ink.flybird.fcommon.event.EventHandler;
import ink.flybird.fcommon.nbt.NBT;
import ink.flybird.fcommon.nbt.NBTTagCompound;
import ink.flybird.fcommon.registry.TypeItem;
import ink.flybird.jflogger.ILogger;
import ink.flybird.jflogger.LogManager;
import net.cubecraft.world.chunk.*;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.Options;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

@TypeItem("cubecraft:chunk_service")
public class ChunkServiceImpl implements Service, ChunkSaver, ChunkLoader {
    public static final Options DEFAULT_LEVELDB_OPTIONS = new Options().createIfMissing(true).blockSize(1024);
    private static final ILogger LOGGER = LogManager.getLogger("server/chunk_service");
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

    public NBTTagCompound loadChunkFromDB(WorldChunkPos pos) {
        while (this.db==null){
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
            LOGGER.error(error);
            return null;
        }
    }

    @EventHandler
    public void onServerWorldInitialized(ServerWorldInitializedEvent e) {
        IWorld world = e.getWorld();
        world.setChunkSaver(this);
        world.setChunkLoader(this);
        WorldGenPipelineBuilder builder = ContentRegistries.CHUNK_GENERATE_PIPELINE.get(world.getId());

        long seed = world.getLevel().getLevelInfo().getSeed();
        ChunkGeneratorPipeline pipeline = builder.build(world.getId(), seed);
        this.pipelineCache.put(world.getId(), pipeline);
    }


    @Override
    public void save(WorldChunk chunk) {
        this.saveTaskPool.submit(() -> {
            while (this.db==null){
                Thread.yield();
            }
            try {
                NBTTagCompound tag = ChunkCodec.getWorldChunkData(chunk);

                ByteArrayOutputStream stream = new ByteArrayOutputStream(4096);
                GZIPOutputStream zipOutput = new GZIPOutputStream(stream);
                NBT.write(tag, new DataOutputStream(zipOutput));
                zipOutput.close();
                stream.close();

                byte[] data = stream.toByteArray();
                byte[] key = (chunk.getWorld().getId() + "@" + chunk.getKey().toString()).getBytes(StandardCharsets.UTF_8);

                this.db.put(key, data);
            } catch (Exception error) {
                LOGGER.error(error);
            }
        });
    }

    @Override
    public void load(IWorld world, ChunkPos pos, ChunkLoadTicket ticket) {
        this.loadTaskPool.submit(() -> {
            NBTTagCompound tag = this.loadChunkFromDB(new WorldChunkPos(world.getId(), pos));
            if (tag != null) {
                WorldChunk chunk = new WorldChunk(world, pos);
                ChunkCodec.setWorldChunkData(chunk, tag);
                world.chunks.add(chunk).addTicket(ticket);
                return;
            }

            ProviderChunk chunk = new ProviderChunk(pos);
            ChunkGeneratorPipeline pipeline = this.pipelineCache.get(world.getId());
            WorldChunk _chunk = new WorldChunk(world, ChunkGenerateTask.createTask(pipeline, chunk).generateChunk());
            world.chunks.add(_chunk).addTicket(ticket);
        });
    }
}
