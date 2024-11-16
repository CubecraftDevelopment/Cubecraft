package net.cubecraft.world.block.access;

import me.gb2022.commons.math.hitting.HitBox;
import net.cubecraft.util.register.Registered;
import net.cubecraft.world.BlockAccessor;
import net.cubecraft.world.biome.Biome;
import net.cubecraft.world.block.Block;
import net.cubecraft.world.block.EnumFacing;

import java.util.Collection;
import java.util.List;

//todo impl
public class SnapshotBlockAccess implements BlockAccess {
    private final int blockId;
    private final byte blockMeta;
    private final byte light;
    private final int biome;

    public SnapshotBlockAccess(int blockId, byte blockMeta, byte light, int biome) {
        this.blockId = blockId;
        this.blockMeta = blockMeta;
        this.light = light;
        this.biome = biome;
    }

    @Override
    public int getBlockId() {
        return this.blockId;
    }

    @Override
    public byte getBlockMeta() {
        return this.blockMeta;
    }

    @Override
    public byte getBlockLight() {
        return this.light;
    }

    @Override
    public Biome getBiome() {
        return null;
    }

    @Override
    public void setBlockMeta(byte meta, boolean sendUpdateEvent) {

    }

    @Override
    public void setBlockLight(byte light, boolean sendUpdateEvent) {

    }

    @Override
    public void setBiome(String biome, boolean sendUpdateEvent) {

    }

    @Override
    public Block getBlock() {
        return null;
    }

    @Override
    public void scheduleTick(int time) {

    }

    @Override
    public long getX() {
        throw new UnsupportedOperationException();
    }

    @Override
    public long getY() {
        throw new UnsupportedOperationException();
    }

    @Override
    public long getZ() {
        throw new UnsupportedOperationException();
    }

    @Override
    public IBlockAccess getNear(EnumFacing facing) {
        return null;
    }

    @Override
    public BlockAccessor getWorld() {
        return null;
    }

    @Override
    public void setBiome(Registered<Biome> biome, boolean sendUpdateEvent) {

    }

    @Override
    public void setBlockId(int id, boolean silent) {

    }

    @Override
    public Collection<HitBox> getHitBox() {
        return List.of();
    }
}
