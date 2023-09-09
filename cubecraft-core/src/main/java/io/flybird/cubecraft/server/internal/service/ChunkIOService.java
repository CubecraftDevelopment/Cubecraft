package io.flybird.cubecraft.server.internal.service;

import io.flybird.cubecraft.register.EnvironmentPath;
import io.flybird.cubecraft.register.SharedContext;
import io.flybird.cubecraft.server.CubecraftServer;
import io.flybird.cubecraft.server.service.AbstractService;
import io.flybird.cubecraft.world.IWorld;
import io.flybird.cubecraft.world.chunk.ChunkCodec;
import io.flybird.cubecraft.world.chunk.ChunkPos;
import io.flybird.cubecraft.world.chunk.WorldChunk;
import ink.flybird.fcommon.GameSetting;
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

//todo:impl
//todo:改你的GameSetting去

@TypeItem("cubecraft:chunk_io")
public class ChunkIOService extends AbstractService {
    public static final Options DEFAULT_LEVELDB_OPTIONS = new Options().createIfMissing(true).blockSize(1024);
    public static final int DEFAULT_DB_LIFETIME = 150;

    private final Logger logger = SharedContext.LOG_CONTEXT.createLogger("chunk_io");
    private final ExecutorService taskPool;
    private final HashMap<String, DB> cachedDatabase = new HashMap<>(16);
    private final HashMap<String, Integer> lifetime = new HashMap<>(16);

    public ChunkIOService(CubecraftServer server, GameSetting setting) {
        super(server);
        this.taskPool = Executors.newFixedThreadPool(setting.getValue("service.io.threads"));
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


    public void loadChunk(IWorld world, ChunkPos pos){
        this.taskPool.submit(() -> {
            WorldChunk chunk=new WorldChunk(world,pos);

            if(loadChunkFromDB(chunk)){
                world.setChunk(chunk);
                return;
            }

            //generate chunk
        });
    }


    //database
    public void tickDataBase(){
        for (String db : this.lifetime.keySet()) {
            int i = this.lifetime.get(db);
            if (i <= 0) {
                this.updateDataBase(db);
                continue;
            }
            this.lifetime.put(db, i - 1);
        }
    }

    public void updateDataBase(String worldID){
        this.closeDataBase(worldID);
        this.getDataBase(worldID);
    }

    public void closeDataBase(String worldID){
        try {
            this.getDataBase(worldID).close();
        } catch (IOException exception) {
            this.logger.exception(exception);
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

    public void saveChunkToDB(WorldChunk chunk) {
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
