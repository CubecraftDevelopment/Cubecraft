package net.cubecraft.world.chunk.storage;

import me.gb2022.commons.nbt.NBTDataIO;
import me.gb2022.commons.math.MathHelper;
import me.gb2022.commons.nbt.NBTTagCompound;

public class ByteDataSection implements NBTDataIO,ByteStorage{
    private ByteStorage storage = new CompressedByteStorage((byte) 0);

    @Override
    public byte get(int x, int y, int z) {
        return 0;
    }

    @Override
    public void set(int x, int y, int z, byte i) {
        DataSection.checkSectionPosition(x, y, z);
        if(this.storage instanceof CompressedByteStorage){
            this.storage=new SimpleByteStorage(this.storage);
        }
        this.storage.set(x, y, z, i);
    }

    public void tryCompress(){
        if(this.storage instanceof CompressedByteStorage){
            return;
        }

        if(storage.get(0,0,0)!= MathHelper.avg(((SimpleByteStorage) storage).getData())){
            return;
        }
        this.storage=new CompressedByteStorage(this.storage);
    }

    @Override
    public NBTTagCompound getData() {
        NBTTagCompound tag=new NBTTagCompound();
        tag.setBoolean("compressed",this.storage instanceof CompressedByteStorage);
        if(this.storage instanceof CompressedByteStorage){
            tag.setByte("data",this.storage.get(0,0,0));
            return tag;
        }
        tag.setByteArray("data", ((SimpleByteStorage) this.storage).getData());
        return tag;
    }

    @Override
    public void setData(NBTTagCompound tag) {
        boolean compressed=tag.getBoolean("compressed");
        if(compressed){
            this.storage=new CompressedByteStorage(tag.getByte("data"));
            return;
        }
        this.storage=new SimpleByteStorage(tag.getByteArray("data"));
    }

    public ByteStorage getStorage() {
        return this.storage;
    }
}
