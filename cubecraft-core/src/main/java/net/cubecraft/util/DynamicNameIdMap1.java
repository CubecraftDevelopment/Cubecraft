package net.cubecraft.util;

import me.gb2022.commons.container.ArrayUtil;
import me.gb2022.commons.container.CollectionUtil;
import me.gb2022.commons.nbt.NBTTagCompound;

import java.util.ArrayList;
import java.util.HashMap;

public class DynamicNameIdMap1 {
    public final HashMap<String, Short> mapping = new HashMap<>();
    public final ArrayList<String> revMap = new ArrayList<>(32);
    public short[] array;
    public short counter;

    public DynamicNameIdMap1(int size) {
        this.array = new short[size];
    }

    public void set(int index, String id) {
        if (!this.mapping.containsKey(id)) {
            this.alloc(id);
        }
        this.array[index] = this.mapping.get(id);
    }

    public void alloc(String id) {
        this.mapping.put(id, this.counter);
        this.revMap.set(this.counter, id);
        ++this.counter;
    }

    public void manageFragment() {
        String[] raw = new String[this.array.length];

        int i;
        for (i = 0; i < this.array.length; ++i) {
            raw[i] = this.revMap.get(this.array[i]);
        }

        this.mapping.clear();
        this.revMap.clear();
        this.counter = 0;

        for (i = 0; i < this.array.length; ++i) {
            this.set(i, raw[i]);
        }

    }

    public String[] getArray() {
        String[] raw = new String[this.array.length];

        for (int i = 0; i < this.array.length; ++i) {
            raw[i] = this.revMap.get(this.array[i]);
        }

        return raw;
    }

    public String get(int index) {
        return this.revMap.get(this.array[index]);
    }

    public void fill(String id) {
        for (int i = 0; i < this.array.length; ++i) {
            this.set(i, id);
        }
    }

    public NBTTagCompound export() {
        NBTTagCompound tag = new NBTTagCompound();
        NBTTagCompound map = new NBTTagCompound();
        CollectionUtil.iterateMap(this.mapping, (key, item) -> map.setInteger(key, item));
        tag.setCompoundTag("map", map);
        tag.setIntArray("data", ArrayUtil.short2int(this.array));
        return tag;
    }

    public void setData(NBTTagCompound tag) {
        this.mapping.clear();
        NBTTagCompound map = tag.getCompoundTag("map");
        CollectionUtil.iterateMap(map.getTagMap(), (key, item) -> this.mapping.put(key, (short) map.getInteger(key)));
        this.array = ArrayUtil.int2short(tag.getIntArray("data"));
        this.manageFragment();
    }

    public void setArr(String[] raw) {
        for (int i = 0; i < this.array.length; ++i) {
            this.set(i, raw[i]);
        }
    }
}
