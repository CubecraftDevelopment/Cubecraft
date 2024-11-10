package net.cubecraft.world.chunk.storage.registry;

import me.gb2022.commons.nbt.NBTTagCompound;
import net.cubecraft.util.register.NamedRegistry;

public class DirectPalette<V> extends StoragePalette<V> {
    public DirectPalette(NamedRegistry<V> registry) {
        super(registry);
    }

    @Override
    public short local(int id) {
        return (short) id;
    }

    @Override
    public int global(short id) {
        return id;
    }

    @Override
    public void gc() {
    }

    @Override
    public NBTTagCompound getData() {
        var nbt = new NBTTagCompound();
        nbt.setInteger("_palette_type", 0);
        return nbt;
    }

    @Override
    public void setData(NBTTagCompound nbtTagCompound) {

    }
}
