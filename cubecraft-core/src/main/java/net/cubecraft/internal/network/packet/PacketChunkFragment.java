package net.cubecraft.internal.network.packet;

import net.cubecraft.net.ByteBufUtil;
import net.cubecraft.net.packet.DataFragmentPacket;
import net.cubecraft.net.packet.PacketConstructor;
import net.cubecraft.world.block.BlockState;
import net.cubecraft.world.block.EnumFacing;
import net.cubecraft.world.block.entity.BlockEntity;
import net.cubecraft.world.chunk.Chunk;
import net.cubecraft.world.chunk.pos.ChunkPos;
import ink.flybird.fcommon.container.ArrayUtil;
import net.cubecraft.util.DynamicNameIdMap;
import ink.flybird.fcommon.nbt.NBT;
import ink.flybird.fcommon.nbt.NBTTagCompound;
import ink.flybird.fcommon.registry.TypeItem;
import ink.flybird.fcommon.timer.TimeCounter;
import io.netty.buffer.ByteBuf;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.HashMap;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

@TypeItem("cubecraft:chunk_data")
public class PacketChunkFragment implements DataFragmentPacket<Chunk> {
    private final TimeCounter counter=new TimeCounter();
    private final DynamicNameIdMap id = new DynamicNameIdMap(ChunkPos.DATA_FRAGMENT_ARRAY_SIZE);
    private final HashMap<String, BlockState> blockEntities = new HashMap<>();

    private byte[] meta = new byte[ChunkPos.DATA_FRAGMENT_ARRAY_SIZE];
    private byte[] facing = new byte[ChunkPos.DATA_FRAGMENT_ARRAY_SIZE];
    private byte[] light = new byte[ChunkPos.DATA_FRAGMENT_ARRAY_SIZE];

    private String world;
    private int layer;
    private long x, z;


    public PacketChunkFragment(String world, int layer, Chunk c) {
        this.layer = layer;
        this.x = c.getKey().getX();
        this.z = c.getKey().getZ();
        this.world = world;
        this.fetchData(c);
    }

    @PacketConstructor
    public PacketChunkFragment() {
    }

    @Override
    public void writePacketData(ByteBuf buffer) throws Exception {
        buffer.writeLong(this.x);
        buffer.writeLong(this.z);
        buffer.writeInt(this.layer);

        NBTTagCompound tag = new NBTTagCompound();
        tag.setTag("block_id", this.id.export());
        tag.setByteArray("block_meta", this.meta);
        tag.setByteArray("block_facing", this.facing);
        tag.setByteArray("light", this.light);
        tag.setString("world", this.world);

        NBTTagCompound blockEntities = new NBTTagCompound();
        for (String loc : this.blockEntities.keySet()) {
            blockEntities.setCompoundTag(loc, this.blockEntities.get(loc).getData());
        }
        tag.setCompoundTag("block_entities", blockEntities);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        GZIPOutputStream g = new GZIPOutputStream(out);
        NBT.write(tag, new DataOutputStream(g));
        g.close();
        ByteBufUtil.writeArray(out.toByteArray(), buffer);
    }

    @Override
    public void readPacketData(ByteBuf buffer) throws Exception {
        this.counter.startTiming();
        this.x = buffer.readLong();
        this.z = buffer.readLong();
        this.layer = buffer.readInt();

        byte[] arr = new byte[buffer.readInt()];
        buffer.readBytes(arr);
        GZIPInputStream in = new GZIPInputStream(new ByteArrayInputStream(arr));
        this.counter.stop();
        //this.logger.info("unzip data time:%dms".formatted(this.counter.getPassedTime()));

        NBTTagCompound tag = (NBTTagCompound) NBT.read(new DataInputStream(in));
        in.close();

        this.counter.startTiming();
        this.world = tag.getString("world");
        this.id.setData(tag.getCompoundTag("block_id"));
        this.facing = tag.getByteArray("block_facing");
        this.meta = tag.getByteArray("block_meta");
        this.light = tag.getByteArray("light");


        NBTTagCompound blockEntities = tag.getCompoundTag("block_entities");
        for (int i = 0; i < Chunk.WIDTH; i++) {
            for (int j = 0; j < Chunk.WIDTH; j++) {
                for (int k = 0; k < Chunk.WIDTH; k++) {
                    String key = "%d/%d/%d".formatted(i, j, k);
                    if (blockEntities.getCompoundTag(key) != null) {
                        BlockEntity be = new BlockEntity("", (byte) 0, (byte) 0);
                        be.setData(blockEntities.getCompoundTag(key));
                        this.blockEntities.put(key, be);
                    }
                }
            }
        }
        this.counter.stop();
       // this.logger.info("read data time:%dms".formatted(this.counter.getPassedTime()));
    }

    public void fetchData(Chunk c) {
        for (int x = 0; x < Chunk.WIDTH; x++) {
            for (int z = 0; z < Chunk.WIDTH; z++) {
                for (int y = 0; y < Chunk.WIDTH; y++) {
                    int pos = ArrayUtil.calcDispatchPos3d(Chunk.WIDTH, Chunk.WIDTH, x, y, z);
                    int absY = y + this.layer * 16;
                    BlockState state = c.getBlockState(x, absY, z);
                    if (state instanceof BlockEntity) {
                        this.blockEntities.put("%s/%s/%s".formatted(x, absY, z), state);
                    }
                    this.id.set(pos, state.getId());
                    this.facing[pos] = state.getFacing().getNumID();
                    this.meta[pos] = state.getMeta();
                    this.light[pos] = c.getBlockLight(x, absY, z);
                }
            }
        }
    }

    public void injectData(Chunk c) {
        this.counter.startTiming();
        for (int x = 0; x < Chunk.WIDTH; x++) {
            for (int z = 0; z < Chunk.WIDTH; z++) {
                for (int y = 0; y < Chunk.WIDTH; y++) {
                    int pos = ArrayUtil.calcDispatchPos3d(Chunk.WIDTH, Chunk.WIDTH, x, y, z);
                    int absY = y + this.layer * 16;

                    if (this.blockEntities.containsKey("%s/%s/%s".formatted(x, absY, z))) {
                        //c.setBlockState(this.blockEntities.getChunk("%s/%s/%s".formatted(x, absY, z)));
                    }
                    c.setBlockID(x, absY, z, this.id.get(pos));
                    c.setBlockFacing(x, absY, z, EnumFacing.fromId(this.facing[pos]));
                    c.setBlockMeta(x, absY, z, this.meta[pos]);
                    c.setBlockLight(x, absY, z, this.light[pos]);
                }
            }
        }
        this.counter.stop();
        //this.logger.info("inject data time:%dms".formatted(this.counter.getPassedTime()));
    }

    public long getX() {
        return x;
    }

    public long getZ() {
        return z;
    }

    public String getWorld() {
        return world;
    }

    public int getLayer() {
        return layer;
    }
}
