package ink.flybird.cubecraft.world.block;

import ink.flybird.cubecraft.register.ContentRegistries;
import ink.flybird.cubecraft.world.IWorld;
import ink.flybird.cubecraft.world.chunk.Chunk;
import ink.flybird.cubecraft.world.chunk.ChunkPos;
import ink.flybird.cubecraft.world.chunk.WorldChunk;
import ink.flybird.cubecraft.world.entity.Entity;
import ink.flybird.cubecraft.world.event.BlockIDChangedEvent;
import ink.flybird.fcommon.math.AABB;
import ink.flybird.fcommon.math.HitBox;
import ink.flybird.fcommon.math.HitResult;
import ink.flybird.fcommon.math.MathHelper;

public class ChunkBlockAccess extends IBlockAccess {
    private final WorldChunk chunk;

    public ChunkBlockAccess(IWorld world, long x, long y, long z, WorldChunk chunk) {
        super(world, x, y, z);
        this.chunk = chunk;
    }

    @Override
    public String getBlockID() {
        String id = this.world.getDimension().predictBlockID(this.world, this.x, this.y, this.z);
        if (id != null) {
            return id;
        } else {
            ChunkPos pos = ChunkPos.fromWorldPos(this.x, this.z);
            return this.chunk.getBlockID(pos.getRelativePosX(x), (int) y, pos.getRelativePosZ(z));
        }
    }

    @Override
    public void setBlockID(String id, boolean sendUpdateEvent) {
        if (y < 0 || y >= Chunk.HEIGHT) {
            return;
        }
        ChunkPos pos = ChunkPos.fromWorldPos(this.x, this.z);
        if (this.chunk == null) {
            return;
        }
        this.chunk.setBlockID(pos.getRelativePosX(x), (int) y, pos.getRelativePosZ(z), id);
        if (!sendUpdateEvent) {
            return;
        }

        for (IBlockAccess blockAccess: this.world.getBlockNeighbor(this.x, this.y, this.z)){
           // blockAccess.getBlock().onBlockUpdate(blockAccess.world,blockAccess.x,blockAccess.y,blockAccess.z);
        }
        this.getBlock().onBlockUpdate(this.world,this.x,this.y,this.z);
        this.world.getEventBus().callEvent(new BlockIDChangedEvent(this.world, this.x, this.y, this.z, getBlockID(), id));

    }

    @Override
    public EnumFacing getBlockFacing() {
        BlockState bs = this.world.getDimension().predictBlockAt(this.world, this.x, this.y, this.z);
        if (bs != null) {
            return bs.getFacing();
        } else {
            ChunkPos pos = ChunkPos.fromWorldPos(this.x, this.z);
            return chunk.getBlockFacing(pos.getRelativePosX(x), (int) y, pos.getRelativePosZ(z));
        }
    }

    @Override
    public void setBlockFacing(EnumFacing facing, boolean sendUpdateEvent) {
        if (y < 0 || y >= Chunk.HEIGHT) {
            return;
        }
        ChunkPos pos = ChunkPos.fromWorldPos(this.x, this.z);
        if (this.chunk == null) {
            return;
        }
        this.chunk.setBlockFacing(pos.getRelativePosX(x), (int) y, pos.getRelativePosZ(z), facing);
    }

    @Override
    public byte getBlockMeta() {
        BlockState bs = this.world.getDimension().predictBlockAt(this.world, this.x, this.y, this.z);
        if (bs != null) {
            return bs.getMeta();
        } else {
            ChunkPos pos = ChunkPos.fromWorldPos(this.x, this.z);
            return this.chunk.getBlockMeta(pos.getRelativePosX(x), (int) y, pos.getRelativePosZ(z));
        }
    }

    @Override
    public void setBlockMeta(byte meta, boolean sendUpdateEvent) {
        if (y < 0 || y >= Chunk.HEIGHT) {
            return;
        }
        ChunkPos pos = ChunkPos.fromWorldPos(this.x, this.z);
        if (this.chunk == null) {
            return;
        }
        this.chunk.setBlockMeta(pos.getRelativePosX(x), (int) y, pos.getRelativePosZ(z), meta);
    }

    @Override
    public byte getBlockLight() {
        byte predictedLight = this.world.getDimension().predictLightAt(this.world, this.x, this.y, this.z);
        if (predictedLight != -1) {
            return predictedLight;
        } else {
            if(this.y>=128){
                return (byte) (getBlock().isSolid()?0:127);
            }
            return (byte) (Math.max(128-(128-this.y)*4,8));

            //ChunkPos pos = ChunkPos.fromWorldPos(this.x, this.z);
            //return this.chunk.getBlockLight(pos.getRelativePosX(this.x), (int) this.y, pos.getRelativePosZ(this.z));
        }
    }

    @Override
    public void setBlockLight(byte light, boolean sendUpdateEvent) {
        if (y < 0 || y >= Chunk.HEIGHT) {
            return;
        }
        ChunkPos pos = ChunkPos.fromWorldPos(this.x, this.z);
        if (this.chunk == null) {
            return;
        }
        this.chunk.setBlockLight(pos.getRelativePosX(x), (int) y, pos.getRelativePosZ(z), light);
    }

    @Override
    public String getBiome() {
        ChunkPos pos = ChunkPos.fromWorldPos(this.x, this.z);
        return this.chunk.getBiome(pos.getRelativePosX(x), (int) this.y, pos.getRelativePosZ(z));
    }

    @Override
    public void setBiome(String biome, boolean sendUpdateEvent) {
        ChunkPos pos = ChunkPos.fromWorldPos(this.x, this.z);
        if (this.chunk == null) {
            return;
        }
        this.chunk.setBiome(pos.getRelativePosX(x), (int) this.y, pos.getRelativePosZ(z), biome);
    }

    @Override
    public byte getTemperature() {
        ChunkPos pos = ChunkPos.fromWorldPos(this.x, this.z);
        return this.chunk.getTemperature(pos.getRelativePosX(x), (int) this.y, pos.getRelativePosZ(z));
    }

    @Override
    public void setTemperature(byte temperature, boolean sendUpdateEvent) {
        ChunkPos pos = ChunkPos.fromWorldPos(this.x, this.z);
        if (this.chunk == null) {
            return;
        }
        this.chunk.setTemperature(pos.getRelativePosX(x), (int) this.y, pos.getRelativePosZ(z), temperature);
    }

    @Override
    public byte getHumidity() {
        ChunkPos pos = ChunkPos.fromWorldPos(this.x, this.z);
        return this.chunk.getHumidity(pos.getRelativePosX(x), (int) this.y, pos.getRelativePosZ(z));
    }

    @Override
    public void setHumidity(byte humidity, boolean sendUpdateEvent) {
        ChunkPos pos = ChunkPos.fromWorldPos(this.x, this.z);
        if (this.chunk == null) {
            return;
        }
        this.chunk.setHumidity(pos.getRelativePosX(x), (int) this.y, pos.getRelativePosZ(z), humidity);
    }

    @Override
    public Block getBlock() {
        return ContentRegistries.BLOCK.get(this.getBlockID());
    }

    @Override
    public AABB[] getCollisionBox() {
        return this.getBlock().getCollisionBox(x, y, z);
    }

    @Override
    public HitBox<Entity, IWorld>[] getSelectionBox() {
        return this.getBlock().getSelectionBox(x, y, z, this);
    }

    @Override
    public void onHit(Entity from, IWorld world, HitResult<Entity, IWorld> hr) {
        this.getBlock().onHit(
                from, world,
                (long) hr.aabb().getPosition().x,
                (long) hr.aabb().getPosition().y,
                (long) hr.aabb().getPosition().z,
                hr.facing()
        );
    }

    @Override
    public void onInteract(Entity from, IWorld world, HitResult<Entity, IWorld> hr) {
        this.getBlock().onInteract(
                from, world,
                (long) hr.aabb().getPosition().x,
                (long) hr.aabb().getPosition().y,
                (long) hr.aabb().getPosition().z,
                hr.facing()
        );
    }
}
