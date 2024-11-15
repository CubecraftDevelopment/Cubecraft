package net.cubecraft.world.chunk.storage.registry;

import me.gb2022.commons.nbt.NBTDataIO;
import me.gb2022.commons.nbt.NBTTagCompound;
import net.cubecraft.util.ArrayCodec;
import net.cubecraft.util.register.NamedRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SuppressWarnings("unchecked")
public final class RegistryStorageContainer<V> implements NBTDataIO {
    public static final Logger LOGGER = LogManager.getLogger("RegistryStorageContainer");

    private final StoragePalette<V>[] palettes;
    private final RegistryStorage<V>[] storages;
    private final boolean[] compressed;
    private final NamedRegistry<V> registry;
    private int capacity;

    public RegistryStorageContainer(NamedRegistry<V> registry, int capacity) {
        var pCap = (int) Math.ceil((double) capacity / 16);

        this.capacity = capacity;
        this.storages = new RegistryStorage[capacity];
        this.palettes = new StoragePalette[pCap];
        this.compressed = new boolean[capacity];
        this.registry = registry;

        for (int i = 0; i < pCap; i++) {
            this.palettes[i] = new RegistryPalette<>(registry);
        }

        for (int i = 0; i < capacity; i++) {
            var s = new SimpleRegistryStorage<V>();
            this.compressed[i] = false;
            this.storages[i] = s;
        }

    }

    private void setStorage(int y, RegistryStorage<V> storage) {
        this.storages[y] = storage;
        this.compressed[y] = storage instanceof CompressedRegistryStorage<V>;
    }

    private void verify(int x, int y, int z) {
        if (x < 0 || x >= 16 || z < 0 || z >= 16 || y < 0 || y >= 16 * this.capacity) {
            throw new IndexOutOfBoundsException();
        }
    }

    public void compress(int position) {
        if (this.compressed[position]) {
            return;
        }

        var storage = this.storages[position];

        if (storage instanceof CompressedRegistryStorage<V>) {
            LOGGER.warn("out-of-sync compressed storage @ {}", position);
            this.compressed[position] = true;
            return;
        }

        var first = storage._get(0, 0, 0);

        for (var b : storage.getData()) {
            if (b != first) {
                return;
            }
        }

        var palette = this.palettes[position >> 4];

        var transformed = new CompressedRegistryStorage<V>((SimpleRegistryStorage<V>) storage);
        palette.transform(storage, transformed);
        this.setStorage(position, transformed);
    }

    public void compress() {
        for (int i = 0; i < this.capacity; i++) {
            this.compress(i);
        }
    }

    public void set(int x, int y, int z, int id) {
        verify(x, y, z);

        var sectionPosition = y >> 4;
        var sect = this.storages[sectionPosition];
        var palette = this.palettes[y >> 8];
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
        var sect = this.storages[sectionPosition];
        var palette = this.palettes[y >> 8];

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
            var section = this.storages[i];
            var compressed = this.compressed[i];

            sect.setBoolean("compressed", compressed);

            if (compressed) {
                sect.setShort("data", section._get(0, 0, 0));
            } else {
                sect.setByteArray("data", ArrayCodec.encode(section.getData()));
            }

            data.setCompoundTag("sect_" + i, sect);
        }

        for (var i = 0; i < this.palettes.length; i++) {
            var palette = this.palettes[i];

            data.setCompoundTag("palette_" + i, palette.getData());
        }

        return data;
    }

    @Override
    public void setData(NBTTagCompound data) {
        this.capacity = data.getInteger("capacity");

        for (var i = 0; i < this.capacity; i++) {
            var sect = data.getCompoundTag("sect_" + i);
            var compressed = sect.getBoolean("compressed");

            if (compressed) {
                this.setStorage(i, new CompressedRegistryStorage<>(sect.getShort("data")));
            } else {
                var bytes = sect.getByteArray("data");
                this.setStorage(i, new SimpleRegistryStorage<>(ArrayCodec.decodeS(bytes)));
            }
        }


        for (var i = 0; i < (int) Math.ceil((double) capacity / 16); i++) {
            var p = data.getCompoundTag("palette_" + i);

            var palette = p.getInteger("_palette_type") == 1 ? new RegistryPalette<>(this.registry) : new DirectPalette<>(this.registry);

            palette.setData(p);

            this.palettes[i] = palette;
        }
    }
}
