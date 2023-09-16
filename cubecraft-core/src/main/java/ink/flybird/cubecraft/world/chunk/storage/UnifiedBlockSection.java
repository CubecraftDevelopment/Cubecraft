package ink.flybird.cubecraft.world.chunk.storage;

import ink.flybird.cubecraft.world.block.EnumFacing;
import ink.flybird.fcommon.file.NBTDataIO;
import ink.flybird.fcommon.nbt.NBTTagCompound;
import ink.flybird.cubecraft.world.chunk.storage.SimpleBlockSection;

public class UnifiedBlockSection implements SectionBlockAccess, NBTDataIO {
    private final String allBlockID;
    private final byte allBlockMeta;
    private final byte allBlockFacing;

    public UnifiedBlockSection(String allBlockID, byte allBlockMeta, byte allBlockFacing) {
        this.allBlockID = allBlockID;
        this.allBlockMeta = allBlockMeta;
        this.allBlockFacing = allBlockFacing;
    }

    public UnifiedBlockSection(NBTTagCompound tag){
        this.allBlockID=tag.getString("block_id");
        this.allBlockFacing=tag.getByte("block_facing");
        this.allBlockMeta=tag.getByte("block_meta");
    }

    public UnifiedBlockSection(SimpleBlockSection section) {
        this.allBlockID = section.getBlockID(0, 0, 0);
        this.allBlockMeta = section.getBlockMeta(0, 0, 0);
        this.allBlockFacing = section.getBlockFacing(0, 0, 0).getNumID();
    }

    //block data
    @Override
    public String getBlockID(int x, int y, int z) {
        DataSection.checkSectionPosition(x, y, z);
        return this.allBlockID;
    }

    @Override
    public EnumFacing getBlockFacing(int x, int y, int z) {
        DataSection.checkSectionPosition(x, y, z);
        return EnumFacing.fromId(this.allBlockFacing);
    }

    @Override
    public byte getBlockMeta(int x, int y, int z) {
        DataSection.checkSectionPosition(x, y, z);
        return this.allBlockMeta;
    }

    @Override
    public void setBlockID(int x, int y, int z, String id) {
        throw new UnsupportedOperationException("full data section,unzip before set value!");
    }

    @Override
    public void setBlockFacing(int x, int y, int z, EnumFacing facing) {
        throw new UnsupportedOperationException("full data section,unzip before set value!");
    }

    @Override
    public void setBlockMeta(int x, int y, int z, byte meta) {
        throw new UnsupportedOperationException("full data section,unzip before set value!");
    }

    @Override
    public NBTTagCompound getData() {
        NBTTagCompound tag=new NBTTagCompound();
        tag.setString("block_id",this.allBlockID);
        tag.setByte("block_facing",this.allBlockFacing);
        tag.setByte("block_meta",this.allBlockMeta);
        return tag;
    }

    @Override
    public void setData(NBTTagCompound tag) {

    }
}