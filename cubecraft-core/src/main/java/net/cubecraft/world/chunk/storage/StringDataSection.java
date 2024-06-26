package net.cubecraft.world.chunk.storage;

import me.gb2022.commons.nbt.NBTDataIO;
import me.gb2022.commons.nbt.NBTTagCompound;

import java.util.Arrays;
import java.util.Objects;

public class StringDataSection implements NBTDataIO, StringStorage {
    private StringStorage storage = new CompressedStringStorage("cubecraft:air");

    @Override
    public String get(int x, int y, int z) {
        DataSection.checkSectionPosition(x, y, z);
        return this.storage.get(x, y, z);
    }

    @Override
    public void set(int x, int y, int z, String i) {
        DataSection.checkSectionPosition(x, y, z);
        if (this.storage instanceof CompressedStringStorage) {
            this.storage = new SimpleStringStorage(this.storage);
        }
        this.storage.set(x, y, z, i);
    }

    public void tryCompress() {
        if (this.storage instanceof CompressedStringStorage) {
            return;
        }

        if (!Arrays.stream(((SimpleStringStorage) this.storage).getArray()).allMatch(s -> Objects.equals(storage.get(0, 0, 0), s))) {
            try {
                ((SimpleStringStorage) this.storage).getMap().manageFragment();
            } catch (Exception e) {
                //todo
            }
            return;
        }
        this.storage = new CompressedStringStorage(this.storage);
    }

    @Override
    public NBTTagCompound getData() {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setBoolean("compressed", this.storage instanceof CompressedStringStorage);
        if (this.storage instanceof CompressedStringStorage) {
            String s = this.storage.get(0, 0, 0);
            if(s==null){
                tag.setString("data","cubecraft:air");
                return tag;
            }
            tag.setString("data", s);
            return tag;
        }
        tag.setCompoundTag("data", ((SimpleStringStorage) this.storage).getData());
        return tag;
    }

    @Override
    public void setData(NBTTagCompound tag) {
        boolean compressed = tag.getBoolean("compressed");
        if (compressed) {
            this.storage = new CompressedStringStorage(tag.getString("data"));
            return;
        }
        this.storage = new SimpleStringStorage();
        ((SimpleStringStorage) this.storage).setData(tag.getCompoundTag("data"));
    }

    public StringStorage getStorage() {
        return this.storage;
    }
}
