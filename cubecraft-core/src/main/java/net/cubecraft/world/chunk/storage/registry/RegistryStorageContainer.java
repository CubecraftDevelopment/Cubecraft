package net.cubecraft.world.chunk.storage.registry;

import me.gb2022.commons.nbt.NBTDataIO;
import me.gb2022.commons.nbt.NBTTagCompound;
import net.cubecraft.util.ArrayCodec;
import net.cubecraft.util.register.NamedRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class RegistryStorageContainer<V> implements NBTDataIO {
    private final List<RegistryPalette<V>> palettes;
    private final List<RegistryStorage<V>> storages;
    private final boolean[] compressed;
    private final NamedRegistry<V> registry;
    private int capacity;

    public RegistryStorageContainer(NamedRegistry<V> registry, int capacity) {
        var pCap = (int) Math.ceil((double) capacity / 16);

        this.capacity = capacity;
        this.storages = new ArrayList<>(capacity);
        this.palettes = new ArrayList<>(pCap);
        this.compressed = new boolean[capacity];
        this.registry = registry;

        for (int i = 0; i < pCap; i++) {
            this.palettes.add(new RegistryPalette<>(registry));
        }

        for (int i = 0; i < capacity; i++) {
            var s = new SimpleRegistryStorage<V>();
            this.compressed[i] = false;
            this.storages.add(0, s);
        }

    }

    private void setStorage(int y, RegistryStorage<V> storage) {
        this.storages.set(y, storage);
        this.compressed[y] = storage instanceof CompressedRegistryStorage<V>;
    }

    private void verify(int x, int y, int z) {
        if (x < 0 || x >= 16 || z < 0 || z >= 16 || y < 0 || y >= 16 * this.capacity) {
            throw new IndexOutOfBoundsException();
        }
    }

    public void set(int x, int y, int z, int id) {
        verify(x, y, z);

        var sectionPosition = y >> 4;
        var sect = this.storages.get(sectionPosition);
        var palette = this.palettes.get(y >> 8);
        var localId = palette.local(id);

        if (!this.compressed[sectionPosition]) {
            sect._set(x, y & 15, z, localId);
            return;
        }

        var changed = sect._get(0, 0, 0) != id;

        if (!changed) {
            return;
        }

        var transformed = new SimpleRegistryStorage<>(((CompressedRegistryStorage<V>) sect));
        palette.transform(sect, transformed);
        this.setStorage(sectionPosition, transformed);

        transformed._set(x, y & 15, z, localId);
    }

    public int get(int x, int y, int z) {
        verify(x, y, z);

        var sectionPosition = y >> 4;
        var sect = this.storages.get(sectionPosition);
        var palette = this.palettes.get(y >> 8);

        return palette.global(sect._get(x, y & 15, z));
    }

    public void set(int x, int y, int z, String id) {
        set(x, y, z, this.registry.id(id));
    }

    @Override
    public NBTTagCompound getData() {
        var data = new NBTTagCompound();

        data.setInteger("capacity", this.capacity);

        for (var i = 0; i < this.capacity; i++) {
            var sect = new NBTTagCompound();
            var section = this.storages.get(i);
            var compressed = this.compressed[i];

            sect.setBoolean("compressed", compressed);

            if (compressed) {
                sect.setShort("data", section._get(0, 0, 0));
            } else {
                sect.setByteArray("data", ArrayCodec.encode(section.getData()));
            }

            data.setCompoundTag("sect_" + i, sect);
        }

        for (var i = 0; i < this.palettes.size(); i++) {
            var p = new NBTTagCompound();
            var palette = this.palettes.get(i);

            //palette.gc();

            p.setShort("allocation", palette.getAllocation());

            var map = palette.getLocal2GlobalMap();

            for (int l : map.keySet()) {
                var id = this.registry.name(map.get(l));
                p.setShort(id, (short) l);
            }

            data.setCompoundTag("palette_" + i, p);
        }

        return data;
    }

    @Override
    public void setData(NBTTagCompound data) {
        this.capacity = data.getInteger("capacity");

        this.storages.clear();

        for (var i = 0; i < this.capacity; i++) {
            this.storages.add(null);

            var sect = data.getCompoundTag("sect_" + i);
            var compressed = sect.getBoolean("compressed");

            if (compressed) {
                this.storages.set(i, new CompressedRegistryStorage<>(sect.getShort("data")));
            } else {
                var bytes = sect.getByteArray("data");
                this.storages.set(i, new SimpleRegistryStorage<>(ArrayCodec.decodeS(bytes)));
            }
        }

        this.palettes.clear();

        for (var i = 0; i < (int) Math.ceil((double) capacity / 16); i++) {
            this.palettes.add(null);

            var p = data.getCompoundTag("palette_" + i);

            var palette = new RegistryPalette<>(this.registry);
            palette.setAllocation(p.getShort("allocation"));

            for (var s : p.getTagMap().keySet()) {
                if (Objects.equals(s, "allocation")) {
                    continue;
                }

                var id = this.registry.id(s);
                var v = p.getShort(s);

                palette.getLocal2GlobalMap().put(v, id);
                palette.getGlobal2localMap().put(id, v);
            }

            this.palettes.set(i, palette);
        }

        return;
    }
}
